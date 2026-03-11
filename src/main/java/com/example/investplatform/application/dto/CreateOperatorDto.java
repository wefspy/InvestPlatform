package com.example.investplatform.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOperatorDto(
        @NotBlank(message = "Email обязательное поле")
        @Email(message = "Email не валидный")
        String email,

        @NotBlank(message = "Пароль обязательное поле")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password,

        @NotBlank(message = "Фамилия обязательное поле")
        String lastName,

        @NotBlank(message = "Имя обязательное поле")
        String firstName,

        String patronymic
) {
}
