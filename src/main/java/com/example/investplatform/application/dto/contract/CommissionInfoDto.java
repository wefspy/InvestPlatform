package com.example.investplatform.application.dto.contract;

import java.math.BigDecimal;

public record CommissionInfoDto(
        BigDecimal rate,
        BigDecimal minAmount,
        BigDecimal maxAmount
) {
}
