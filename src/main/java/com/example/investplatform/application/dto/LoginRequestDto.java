package com.example.investplatform.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на аутентификацию")
public record LoginRequestDto(
        @Schema(description = "Имя пользователя")
        @NotBlank(message = "Username обязательное поле")
        String username,

        @Schema(description = "Пароль")
        @NotBlank(message = "Пароль не может быть пустыми")
        String password
) {
}
