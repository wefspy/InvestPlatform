package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.payment.DepositRequestDto;
import com.example.investplatform.application.dto.payment.PaymentResponseDto;
import com.example.investplatform.application.dto.payment.yookassa.YookassaAmountDto;
import com.example.investplatform.application.dto.payment.yookassa.YookassaConfirmationDto;
import com.example.investplatform.application.dto.payment.yookassa.YookassaPaymentCreateRequest;
import com.example.investplatform.application.dto.payment.yookassa.YookassaPaymentResponse;
import com.example.investplatform.application.dto.payment.yookassa.YookassaWebhookNotification;
import com.example.investplatform.application.exception.YookassaApiException;
import com.example.investplatform.application.exception.PaymentNotFoundException;
import com.example.investplatform.application.exception.WebhookProcessingException;
import com.example.investplatform.infrastructure.client.YookassaClient;
import com.example.investplatform.infrastructure.config.property.YookassaProperties;
import com.example.investplatform.infrastructure.repository.EmitentRepository;
import com.example.investplatform.infrastructure.repository.InvestorRepository;
import com.example.investplatform.infrastructure.repository.PaymentRepository;
import com.example.investplatform.infrastructure.repository.PersonalAccountRepository;
import com.example.investplatform.infrastructure.repository.AccountTransactionRepository;
import com.example.investplatform.model.entity.account.AccountTransaction;
import com.example.investplatform.model.entity.account.PersonalAccount;
import com.example.investplatform.model.entity.payment.Payment;
import com.example.investplatform.model.enums.PaymentDirection;
import com.example.investplatform.model.enums.PaymentType;
import com.example.investplatform.model.enums.TransactionType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String STATUS_SUCCEEDED = "succeeded";
    private static final String STATUS_CANCELED = "canceled";
    private static final String PAYMENT_EVENT_PREFIX = "payment.";

    private final YookassaClient yookassaClient;
    private final YookassaProperties yookassaProperties;
    private final PaymentRepository paymentRepository;
    private final PersonalAccountRepository personalAccountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final InvestorRepository investorRepository;
    private final EmitentRepository emitentRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public PaymentResponseDto initDeposit(Long userId, DepositRequestDto dto) {
        PersonalAccount account = findPersonalAccount(userId);

        String currency = yookassaProperties.getCurrency();
        String idempotencyKey = UUID.randomUUID().toString();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("personalAccountId", String.valueOf(account.getId()));
        metadata.put("userId", String.valueOf(userId));

        String description = dto.description() != null && !dto.description().isBlank()
                ? dto.description()
                : "Пополнение лицевого счёта %s".formatted(account.getAccountNumber());

        YookassaPaymentCreateRequest request = new YookassaPaymentCreateRequest(
                YookassaAmountDto.of(dto.amount(), currency),
                description,
                YookassaConfirmationDto.redirect(yookassaProperties.getReturnUrl()),
                true,
                metadata
        );

        YookassaPaymentResponse response = yookassaClient.createPayment(request, idempotencyKey);
        log.info("Создан платёж ЮKassa {} со статусом {} для accountId={}",
                response.id(), response.status(), account.getId());

        Payment payment = Payment.builder()
                .yukassaPaymentId(response.id())
                .personalAccount(account)
                .paymentType(PaymentType.DEPOSIT)
                .direction(PaymentDirection.INBOUND)
                .amount(response.amount().toBigDecimal())
                .currency(response.amount().currency())
                .yukassaStatus(response.status())
                .paymentMethodType(response.paymentMethod() != null
                        ? response.paymentMethod().type() : "pending")
                .description(description)
                .yukassaMetadata(toMap(response))
                .idempotencyKey(idempotencyKey)
                .build();

        payment = paymentRepository.save(payment);

        String confirmationUrl = response.confirmation() != null
                ? response.confirmation().confirmationUrl() : null;

        return toResponseDto(payment, confirmationUrl);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Платёж с ID %d не найден".formatted(paymentId)));
        return toResponseDto(payment, null);
    }

    /**
     * Обрабатывает webhook-уведомление от ЮKassa.
     * Тело webhook используется только как триггер: для авторитативного статуса
     * платёж перезапрашивается через API ЮKassa по id — так подделать уведомление
     * не получится даже при обходе IP-фильтра.
     */
    @Transactional
    public void applyWebhook(YookassaWebhookNotification notification) {
        JsonNode object = notification.object();
        if (object == null || !object.has("id")) {
            throw new WebhookProcessingException("Webhook ЮKassa не содержит object.id");
        }

        String event = notification.event();
        if (event == null || !event.startsWith(PAYMENT_EVENT_PREFIX)) {
            log.info("Событие {} не обрабатывается PaymentService", event);
            return;
        }

        String yukassaPaymentId = object.get("id").asText();
        applyPaymentStateFromApi(yukassaPaymentId);
    }

    /**
     * Перезапрашивает платёж через API ЮKassa и приводит локальное состояние к ответу API.
     * Вызывается из webhook-обработчика и может использоваться вручную для реконсиляции.
     */
    @Transactional
    public void applyPaymentStateFromApi(String yukassaPaymentId) {
        Payment payment = paymentRepository.findByYukassaPaymentId(yukassaPaymentId)
                .orElseThrow(() -> new WebhookProcessingException(
                        "Платёж ЮKassa %s не найден в БД".formatted(yukassaPaymentId)));

        YookassaPaymentResponse apiResponse;
        try {
            apiResponse = yookassaClient.getPayment(yukassaPaymentId);
        } catch (YookassaApiException e) {
            throw new WebhookProcessingException(
                    "Не удалось подтвердить платёж %s через API ЮKassa".formatted(yukassaPaymentId), e);
        }

        String newStatus = apiResponse.status();
        if (newStatus == null) {
            throw new WebhookProcessingException(
                    "Ответ API ЮKassa не содержит статуса для платежа " + yukassaPaymentId);
        }

        if (newStatus.equals(payment.getYukassaStatus())
                && (payment.getPaidAt() != null || payment.getCanceledAt() != null)) {
            log.info("Статус платежа {} уже финализирован как {}, пропуск",
                    yukassaPaymentId, newStatus);
            return;
        }

        JsonNode object = objectMapper.valueToTree(apiResponse);

        payment.setYukassaStatus(newStatus);
        payment.setYukassaMetadata(jsonNodeToMap(object));

        if (apiResponse.paymentMethod() != null && apiResponse.paymentMethod().type() != null) {
            payment.setPaymentMethodType(apiResponse.paymentMethod().type());
        }

        if (STATUS_SUCCEEDED.equals(newStatus)) {
            handleSucceeded(payment, object);
        } else if (STATUS_CANCELED.equals(newStatus)) {
            handleCanceled(payment, object);
        } else {
            log.info("Платёж {} в промежуточном статусе {}", yukassaPaymentId, newStatus);
        }

        paymentRepository.save(payment);
    }

    private void handleSucceeded(Payment payment, JsonNode object) {
        if (payment.getPaidAt() != null) {
            log.info("Платёж {} уже был проведён, повторное зачисление пропущено",
                    payment.getYukassaPaymentId());
            return;
        }

        PersonalAccount account = payment.getPersonalAccount();
        account.setBalance(account.getBalance().add(payment.getAmount()));
        personalAccountRepository.save(account);

        AccountTransaction transaction = AccountTransaction.builder()
                .personalAccount(account)
                .transactionType(TransactionType.DEPOSIT)
                .amount(payment.getAmount())
                .balanceAfter(account.getBalance())
                .payment(payment)
                .description("Пополнение через ЮKassa, платёж %s".formatted(payment.getYukassaPaymentId()))
                .build();
        transactionRepository.save(transaction);

        payment.setPaidAt(resolveTimestamp(object, "captured_at", LocalDateTime.now()));

        if (object.has("receipt") && object.get("receipt").has("url")) {
            payment.setReceiptUrl(object.get("receipt").get("url").asText());
        }

        log.info("Платёж {} проведён, accountId={} пополнен на {} {}",
                payment.getYukassaPaymentId(), account.getId(),
                payment.getAmount(), payment.getCurrency());
    }

    private void handleCanceled(Payment payment, JsonNode object) {
        payment.setCanceledAt(resolveTimestamp(object, "canceled_at", LocalDateTime.now()));

        if (object.has("cancellation_details")) {
            JsonNode details = object.get("cancellation_details");
            String party = details.has("party") ? details.get("party").asText() : "";
            String reason = details.has("reason") ? details.get("reason").asText() : "";
            payment.setCancellationReason("%s: %s".formatted(party, reason));
        }

        log.info("Платёж {} отменён: {}",
                payment.getYukassaPaymentId(), payment.getCancellationReason());
    }

    private LocalDateTime resolveTimestamp(JsonNode object, String field, LocalDateTime fallback) {
        if (object.has(field) && !object.get(field).isNull()) {
            try {
                return java.time.OffsetDateTime.parse(object.get(field).asText())
                        .atZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime();
            } catch (Exception e) {
                log.warn("Не удалось разобрать поле {}: {}", field, e.getMessage());
            }
        }
        return fallback;
    }

    private PersonalAccount findPersonalAccount(Long userId) {
        return investorRepository.findByUserId(userId)
                .map(investor -> investor.getPersonalAccount())
                .or(() -> emitentRepository.findByUserId(userId)
                        .map(emitent -> emitent.getPersonalAccount()))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Лицевой счёт для пользователя с ID %d не найден".formatted(userId)));
    }

    private Map<String, Object> toMap(YookassaPaymentResponse response) {
        return objectMapper.convertValue(response, new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});
    }

    private Map<String, Object> jsonNodeToMap(JsonNode node) {
        return objectMapper.convertValue(node, new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});
    }

    private PaymentResponseDto toResponseDto(Payment payment, String confirmationUrl) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getYukassaPaymentId(),
                payment.getYukassaStatus(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethodType(),
                payment.getDescription(),
                confirmationUrl,
                payment.getPaidAt(),
                payment.getCreatedAt()
        );
    }
}
