package com.example.investplatform.application.dto.contract;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentContractResponseDto(
        Long id,
        String contractNumber,
        Long proposalId,
        String proposalTitle,
        Long investorId,
        String statusCode,
        String statusName,
        BigDecimal amount,
        String rejectionReason,
        String withdrawalReason,
        LocalDateTime signedAt,
        LocalDateTime reviewedAt,
        LocalDateTime withdrawnAt,
        LocalDateTime completedAt,
        LocalDateTime failedAt,
        LocalDateTime createdAt,
        LocalDateTime withdrawalDeadline
) {
}
