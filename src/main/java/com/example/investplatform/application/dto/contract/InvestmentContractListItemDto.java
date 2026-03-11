package com.example.investplatform.application.dto.contract;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentContractListItemDto(
        Long id,
        String contractNumber,
        Long proposalId,
        String proposalTitle,
        String statusCode,
        String statusName,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
