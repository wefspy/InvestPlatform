package com.example.investplatform.application.dto.proposal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProposalCatalogFilterDto(
        String q,
        String investmentMethodCode,
        Long emitentId,
        BigDecimal minInvestmentAmountFrom,
        BigDecimal minInvestmentAmountTo,
        BigDecimal maxInvestmentAmountFrom,
        BigDecimal maxInvestmentAmountTo,
        BigDecimal pricePerUnitFrom,
        BigDecimal pricePerUnitTo,
        LocalDate endDateFrom,
        LocalDate endDateTo,
        Boolean hasPreemptiveRight,
        Boolean onlyAvailable
) {
    public ProposalCatalogFilterDto {
        if (q != null) {
            String trimmed = q.trim();
            q = trimmed.isEmpty() ? null : trimmed;
        }
        if (investmentMethodCode != null) {
            String trimmed = investmentMethodCode.trim();
            investmentMethodCode = trimmed.isEmpty() ? null : trimmed;
        }
        if (onlyAvailable == null) {
            onlyAvailable = Boolean.FALSE;
        }
    }
}
