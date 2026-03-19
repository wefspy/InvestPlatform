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

        @DecimalMin(value = "0.0001", message = "Цена за одну ценную бумагу должна быть больше 0")
        BigDecimal pricePerUnit,

        @Min(value = 1, message = "Общее количество ценных бумаг должно быть больше 0")
        Long totalQuantity,

        @Min(value = 1, message = "Минимальное количество для покупки должно быть больше 0")
        Long minPurchaseQuantity,

        @Min(value = 1, message = "Максимальное количество для покупки должно быть больше 0")
        Long maxPurchaseQuantity,

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
