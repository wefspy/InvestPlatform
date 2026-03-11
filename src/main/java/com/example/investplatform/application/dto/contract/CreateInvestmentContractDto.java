package com.example.investplatform.application.dto.contract;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateInvestmentContractDto(

        @NotNull(message = "ID инвестиционного предложения обязателен")
        Long proposalId,

        @NotNull(message = "Сумма инвестирования обязательна")
        @DecimalMin(value = "0.01", message = "Сумма инвестирования должна быть больше 0")
        BigDecimal amount
) {
}
