package com.example.investplatform.application.dto.payment.yookassa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YookassaRefundResponse(
        String id,
        String status,
        @JsonProperty("payment_id") String paymentId,
        YookassaAmountDto amount,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        String description,
        @JsonProperty("cancellation_details") YookassaCancellationDetailsDto cancellationDetails
) {
}
