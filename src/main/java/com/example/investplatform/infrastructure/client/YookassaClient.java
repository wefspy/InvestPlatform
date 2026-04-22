package com.example.investplatform.infrastructure.client;

import com.example.investplatform.application.dto.payment.yookassa.YookassaPaymentCreateRequest;
import com.example.investplatform.application.dto.payment.yookassa.YookassaPaymentResponse;
import com.example.investplatform.application.dto.payment.yookassa.YookassaRefundCreateRequest;
import com.example.investplatform.application.dto.payment.yookassa.YookassaRefundResponse;
import com.example.investplatform.application.exception.YookassaApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class YookassaClient {

    private static final String IDEMPOTENCE_HEADER = "Idempotence-Key";
    private static final int MAX_LOGGED_BODY = 500;

    private final RestClient yookassaRestClient;

    private static String truncate(String value) {
        if (value == null) {
            return "";
        }
        return value.length() <= MAX_LOGGED_BODY
                ? value
                : value.substring(0, MAX_LOGGED_BODY) + "...<truncated>";
    }

    public YookassaPaymentResponse createPayment(YookassaPaymentCreateRequest request, String idempotencyKey) {
        try {
            return yookassaRestClient.post()
                    .uri("/payments")
                    .header(IDEMPOTENCE_HEADER, idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(YookassaPaymentResponse.class);
        } catch (RestClientResponseException e) {
            log.error("Создание платежа ЮKassa завершилось ошибкой {}: {}",
                    e.getStatusCode(), truncate(e.getResponseBodyAsString()));
            throw new YookassaApiException(
                    e.getStatusCode().value(),
                    "Не удалось создать платёж: " + truncate(e.getResponseBodyAsString()),
                    e
            );
        }
    }

    public YookassaPaymentResponse getPayment(String paymentId) {
        try {
            return yookassaRestClient.get()
                    .uri("/payments/{id}", paymentId)
                    .retrieve()
                    .body(YookassaPaymentResponse.class);
        } catch (RestClientResponseException e) {
            log.error("Получение платежа {} завершилось ошибкой {}: {}",
                    paymentId, e.getStatusCode(), truncate(e.getResponseBodyAsString()));
            throw new YookassaApiException(
                    e.getStatusCode().value(),
                    "Не удалось получить платёж %s: %s".formatted(paymentId, truncate(e.getResponseBodyAsString())),
                    e
            );
        }
    }

    public YookassaRefundResponse createRefund(YookassaRefundCreateRequest request, String idempotencyKey) {
        try {
            return yookassaRestClient.post()
                    .uri("/refunds")
                    .header(IDEMPOTENCE_HEADER, idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(YookassaRefundResponse.class);
        } catch (RestClientResponseException e) {
            log.error("Создание возврата ЮKassa завершилось ошибкой {}: {}",
                    e.getStatusCode(), truncate(e.getResponseBodyAsString()));
            throw new YookassaApiException(
                    e.getStatusCode().value(),
                    "Не удалось создать возврат: " + truncate(e.getResponseBodyAsString()),
                    e
            );
        }
    }

    public YookassaRefundResponse getRefund(String refundId) {
        try {
            return yookassaRestClient.get()
                    .uri("/refunds/{id}", refundId)
                    .retrieve()
                    .body(YookassaRefundResponse.class);
        } catch (RestClientResponseException e) {
            log.error("Получение возврата {} завершилось ошибкой {}: {}",
                    refundId, e.getStatusCode(), truncate(e.getResponseBodyAsString()));
            throw new YookassaApiException(
                    e.getStatusCode().value(),
                    "Не удалось получить возврат %s: %s".formatted(refundId, truncate(e.getResponseBodyAsString())),
                    e
            );
        }
    }
}
