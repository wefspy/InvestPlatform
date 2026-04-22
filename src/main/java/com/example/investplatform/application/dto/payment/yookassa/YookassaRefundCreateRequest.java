package com.example.investplatform.application.dto.payment.yookassa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record YookassaRefundCreateRequest(
        @JsonProperty("payment_id") String paymentId,
        YookassaAmountDto amount,
        String description
) {
}
