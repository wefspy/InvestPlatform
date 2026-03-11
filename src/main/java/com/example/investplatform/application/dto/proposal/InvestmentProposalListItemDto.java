package com.example.investplatform.application.dto.proposal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvestmentProposalListItemDto(
        Long id,
        Long emitentId,
        String statusCode,
        String statusName,
        String title,
        BigDecimal maxInvestmentAmount,
        BigDecimal minInvestmentAmount,
        BigDecimal collectedAmount,
        LocalDate proposalStartDate,
        LocalDate proposalEndDate,
        LocalDateTime createdAt
) {
}
