package com.example.investplatform.application.dto.proposal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvestmentProposalResponseDto(
        Long id,
        Long emitentId,
        String statusCode,
        String statusName,
        String investmentMethodCode,
        String title,
        String investmentGoals,
        String goalRiskFactors,
        String emitentRisks,
        String investmentRisks,
        String issueDecisionInfo,
        String placementProcedure,
        String placementTerms,
        String placementConditions,
        Boolean hasPreemptiveRight,
        String preemptiveRightDetails,
        String riskWarning,
        BigDecimal maxInvestmentAmount,
        BigDecimal minInvestmentAmount,
        LocalDate proposalStartDate,
        LocalDate proposalEndDate,
        String essentialContractTerms,
        String expertMonitoringInfo,
        Boolean hasPropertyRightsCondition,
        String propertyRightsDetails,
        String applicableLaw,
        String suspensiveConditions,
        BigDecimal collectedAmount,
        String rejectionReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        LocalDateTime activatedAt,
        LocalDateTime closedAt
) {
}
