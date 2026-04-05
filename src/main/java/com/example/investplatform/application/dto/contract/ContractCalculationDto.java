package com.example.investplatform.application.dto.contract;

import java.math.BigDecimal;

public record ContractCalculationDto(
        Long securitiesQuantity,
        BigDecimal pricePerUnit,
        BigDecimal investmentAmount,
        BigDecimal commissionAmount,
        BigDecimal totalAmount
) {
}
