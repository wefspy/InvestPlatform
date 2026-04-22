package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.payment.yookassa.YookassaWebhookNotification;
import com.example.investplatform.application.exception.WebhookProcessingException;
import com.example.investplatform.infrastructure.repository.YukassaWebhookRepository;
import com.example.investplatform.model.entity.payment.YukassaWebhook;
import com.example.investplatform.model.enums.WebhookProcessingStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class YookassaWebhookService {

    private final YukassaWebhookRepository webhookRepository;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handle(YookassaWebhookNotification notification) {
        if (notification == null || notification.event() == null || notification.object() == null) {
            throw new WebhookProcessingException("Некорректное webhook-уведомление ЮKassa");
        }

        JsonNode object = notification.object();
        String objectId = object.has("id") ? object.get("id").asText() : null;
        if (objectId == null) {
            throw new WebhookProcessingException("Webhook не содержит object.id");
        }

        String eventId = "%s:%s".formatted(notification.event(), objectId);

        if (webhookRepository.existsByEventId(eventId)) {
            log.info("Webhook {} уже обработан, пропускаем", eventId);
            return;
        }

        YukassaWebhook webhook = YukassaWebhook.builder()
                .eventId(eventId)
                .eventType(notification.event())
                .objectType(notification.type() != null ? notification.type() : "unknown")
                .objectId(objectId)
                .payload(objectMapper.convertValue(
                        notification,
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}))
                .processingStatus(WebhookProcessingStatus.PENDING)
                .attempts(1)
                .build();

        webhook = webhookRepository.save(webhook);

        try {
            paymentService.applyWebhook(notification);
            webhook.setProcessingStatus(WebhookProcessingStatus.PROCESSED);
            webhook.setProcessedAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Не удалось обработать webhook {}: {}", eventId, e.getMessage(), e);
            webhook.setProcessingStatus(WebhookProcessingStatus.FAILED);
            webhook.setErrorMessage(e.getMessage());
            webhook.setProcessedAt(LocalDateTime.now());
            webhookRepository.save(webhook);
            throw new WebhookProcessingException(
                    "Ошибка обработки webhook " + eventId, e);
        }

        webhookRepository.save(webhook);
    }

    @SuppressWarnings("unused")
    private static String generateEventId() {
        return UUID.randomUUID().toString();
    }
}
