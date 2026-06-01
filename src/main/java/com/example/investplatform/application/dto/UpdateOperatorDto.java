package com.example.investplatform.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateOperatorDto(
        @NotBlank(message = "Фамилия обязательное поле")
        String lastName,

        @NotBlank(message = "Имя обязательное поле")
        String firstName,

        String patronymic
) {
}
