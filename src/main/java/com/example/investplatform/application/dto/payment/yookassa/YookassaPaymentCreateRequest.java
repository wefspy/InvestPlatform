package com.example.investplatform.application.dto.payment.yookassa;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record YookassaPaymentCreateRequest(
        YookassaAmountDto amount,
        String description,
        YookassaConfirmationDto confirmation,
        Boolean capture,
        Map<String, String> metadata
) {
}
