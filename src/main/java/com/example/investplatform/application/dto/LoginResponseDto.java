package com.example.investplatform.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Аутентифицированный пользователь")
public record LoginResponseDto(
        @Schema(description = "Идентификатор пользователя")
        Long userId,

        @Schema(description = "Список ролей пользователя")
        Set<String> roles,

        @Schema(description = "Отображаемое имя: ФИО для физ. лиц / название организации для юр. лиц")
        String displayName,

        @Schema(description = "Токен для доступа к закрытым REST API методам (null если требуется 2FA)")
        String accessToken,

        @Schema(description = "Токен для обновления accessToken по истечении его срока годности (null если требуется 2FA)")
        String refreshToken,

        @Schema(description = "Требуется ли подтверждение входа через 2FA")
        Boolean requires2fa,

        @Schema(description = "Временный токен для подтверждения 2FA (действует 5 минут)")
        String twoFactorToken
) {
}
