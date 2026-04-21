package com.example.investplatform.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "TOTP-код из приложения-аутентификатора")
public record TwoFactorCodeDto(
        @Schema(description = "6-значный код", example = "123456")
        @NotBlank(message = "Код обязателен")
        @Pattern(regexp = "\\d{6}", message = "Код должен содержать ровно 6 цифр")
        String code
) {
}
