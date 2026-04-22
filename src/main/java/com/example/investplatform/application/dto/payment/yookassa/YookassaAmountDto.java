package com.example.investplatform.application.dto.payment.yookassa;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record YookassaAmountDto(
        String value,
        String currency
) {
    public static YookassaAmountDto of(BigDecimal value, String currency) {
        return new YookassaAmountDto(value.toPlainString(), currency);
    }

    public BigDecimal toBigDecimal() {
        return new BigDecimal(value);
    }
}
