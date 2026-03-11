package com.example.investplatform.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @NotBlank(message = "Username обязательное поле")
        String username,

        @NotBlank(message = "Пароль обязательное поле")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password,

        @Email(message = "Email не валидный")
        String email,

        @Size(min = 10, max = 15, message = "Номер телефона должен содержать от 10 до 15 символов")
        String phoneNumber
) {
}
