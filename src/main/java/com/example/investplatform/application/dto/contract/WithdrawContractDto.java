package com.example.investplatform.application.dto.contract;

import jakarta.validation.constraints.NotBlank;

public record WithdrawContractDto(

        @NotBlank(message = "Причина отзыва обязательна")
        String reason
) {
}
