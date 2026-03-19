package com.example.investplatform.application.dto.proposal;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInvestmentProposalDto(

        @NotBlank(message = "Код способа инвестирования обязателен")
        String investmentMethodCode,

        @NotBlank(message = "Название инвестиционного предложения обязательно")
        @Size(max = 500, message = "Название не должно превышать 500 символов")
        String title,

        @NotBlank(message = "Цели инвестирования обязательны")
        String investmentGoals,

        @NotBlank(message = "Факторы риска целей обязательны")
        String goalRiskFactors,

        @NotBlank(message = "Риски эмитента обязательны")
        String emitentRisks,

        @NotBlank(message = "Инвестиционные риски обязательны")
        String investmentRisks,

        String issueDecisionInfo,

        String placementProcedure,

        String placementTerms,

        String placementConditions,

        Boolean hasPreemptiveRight,

        String preemptiveRightDetails,

        @NotBlank(message = "Предупреждение о рисках обязательно")
        String riskWarning,

        @NotNull(message = "Максимальная сумма инвестирования обязательна")
        @DecimalMin(value = "0.01", message = "Максимальная сумма инвестирования должна быть больше 0")
        BigDecimal maxInvestmentAmount,

        @NotNull(message = "Минимальная сумма инвестирования обязательна")
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

        @NotNull(message = "Дата начала сбора обязательна")
        @Future(message = "Дата начала сбора должна быть в будущем")
        LocalDate proposalStartDate,

        @NotNull(message = "Дата окончания сбора обязательна")
        @Future(message = "Дата окончания сбора должна быть в будущем")
        LocalDate proposalEndDate,

        @NotBlank(message = "Существенные условия договора обязательны")
        String essentialContractTerms,

        String expertMonitoringInfo,

        @NotNull(message = "Признак наличия условия об имущественных правах обязателен")
        Boolean hasPropertyRightsCondition,

        String propertyRightsDetails,

        @Size(max = 500, message = "Применимое право не должно превышать 500 символов")
        String applicableLaw,

        String suspensiveConditions
) {
}
