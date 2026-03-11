package com.example.investplatform.application.dto.proposal;

import jakarta.validation.constraints.NotBlank;

public record ChangeProposalStatusDto(

        @NotBlank(message = "Код нового статуса обязателен")
        String statusCode,

        String comment
) {
}
