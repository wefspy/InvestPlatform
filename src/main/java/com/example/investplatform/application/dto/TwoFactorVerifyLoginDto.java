package com.example.investplatform.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Подтверждение входа с 2FA")
public record TwoFactorVerifyLoginDto(
        @Schema(description = "Временный токен, полученный при логине")
        @NotBlank(message = "Токен обязателен")
        String twoFactorToken,

        @Schema(description = "6-значный код из приложения-аутентификатора", example = "123456")
        @NotBlank(message = "Код обязателен")
        @Pattern(regexp = "\\d{6}", message = "Код должен содержать ровно 6 цифр")
        String code
) {
}
