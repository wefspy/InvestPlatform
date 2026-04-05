package com.example.investplatform.application.dto.contract;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateInvestmentContractDto(

        @NotNull(message = "ID инвестиционного предложения обязателен")
        Long proposalId,

        @NotNull(message = "Количество ценных бумаг обязательно")
        @Min(value = 1, message = "Количество ценных бумаг должно быть больше 0")
        Long securitiesQuantity
) {
}
