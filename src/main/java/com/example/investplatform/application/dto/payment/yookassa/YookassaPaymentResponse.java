package com.example.investplatform.application.dto.payment.yookassa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YookassaPaymentResponse(
        String id,
        String status,
        YookassaAmountDto amount,
        String description,
        @JsonProperty("payment_method") YookassaPaymentMethodDto paymentMethod,
        YookassaConfirmationDto confirmation,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("captured_at") OffsetDateTime capturedAt,
        @JsonProperty("expires_at") OffsetDateTime expiresAt,
        Boolean paid,
        Boolean refundable,
        @JsonProperty("refunded_amount") YookassaAmountDto refundedAmount,
        @JsonProperty("cancellation_details") YookassaCancellationDetailsDto cancellationDetails,
        @JsonProperty("receipt_registration") String receiptRegistration,
        Map<String, String> metadata
) {
}
