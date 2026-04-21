package com.example.investplatform.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Данные для настройки 2FA")
public record TwoFactorSetupResponseDto(
        @Schema(description = "Секретный ключ (Base32) для ручного ввода в приложение-аутентификатор")
        String secret,

        @Schema(description = "URI для генерации QR-кода (otpauth://totp/...)")
        String qrUri
) {
}
