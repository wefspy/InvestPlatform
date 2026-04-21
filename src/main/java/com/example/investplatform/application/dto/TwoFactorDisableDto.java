package com.example.investplatform.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Запрос на отключение 2FA")
public record TwoFactorDisableDto(
        @Schema(description = "Текущий пароль пользователя")
        @NotBlank(message = "Пароль обязателен")
        String password,

        @Schema(description = "6-значный код из приложения-аутентификатора", example = "123456")
        @NotBlank(message = "Код обязателен")
        @Pattern(regexp = "\\d{6}", message = "Код должен содержать ровно 6 цифр")
        String code
) {
}
