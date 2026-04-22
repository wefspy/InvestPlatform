package com.example.investplatform.application.dto.payment.yookassa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YookassaPaymentMethodDto(
        String type,
        String id,
        Boolean saved
) {
}
