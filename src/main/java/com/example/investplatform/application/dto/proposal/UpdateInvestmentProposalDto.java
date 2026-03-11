package com.example.investplatform.application.dto.proposal;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateInvestmentProposalDto(

        @Size(max = 500, message = "Название не должно превышать 500 символов")
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

        @DecimalMin(value = "0.01", message = "Максимальная сумма инвестирования должна быть больше 0")
        BigDecimal maxInvestmentAmount,

        @DecimalMin(value = "0.01", message = "Минимальная сумма инвестирования должна быть больше 0")
        BigDecimal minInvestmentAmount,

        LocalDate proposalStartDate,

        LocalDate proposalEndDate,

        String essentialContractTerms,

        String expertMonitoringInfo,

        Boolean hasPropertyRightsCondition,

        String propertyRightsDetails,

        @Size(max = 500, message = "Применимое право не должно превышать 500 символов")
        String applicableLaw,

        String suspensiveConditions
) {
}
