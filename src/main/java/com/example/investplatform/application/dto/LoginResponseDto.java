package com.example.investplatform.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Аутентифицированный пользователь")
public record LoginResponseDto(
        @Schema(description = "Идентификатор пользователя")
        Long userId,

        @Schema(description = "Список ролей пользователя")
        Set<String> roles,

        @Schema(description = "Токен для доступа к закрытым REST API методам")
        String accessToken,

        @Schema(description = "Токен для обновления accessToken по истечении его срока годности")
        String refreshToken
) {
}
