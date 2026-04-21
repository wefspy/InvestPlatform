package com.example.investplatform.controller;

import com.example.investplatform.application.dto.*;
import com.example.investplatform.application.service.AuthService;
import com.example.investplatform.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Аутентификация пользователя (логин)",
            description = "Если у пользователя включена 2FA, возвращает requires2fa=true и временный twoFactorToken. "
                    + "Для завершения входа необходимо вызвать /api/auth/2fa/verify.")
    @ApiResponse(responseCode = "200", description = "Успешная аутентификация (или требуется 2FA)", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "401", description = "Неверные учетные данные", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }

    @Operation(summary = "Обновление access/refresh токенов по refresh токену")
    @ApiResponse(responseCode = "200", description = "Токены успешно обновлены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))
    })
    @ApiResponse(responseCode = "401", description = "Неверный или просроченный refresh токен", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestParam String refreshToken) {
        LoginResponseDto loginResponseDto = authService.refresh(refreshToken);
        return ResponseEntity.ok(loginResponseDto);
    }

    // ========================= 2FA =========================

    @Operation(summary = "Подтверждение входа с 2FA",
            description = "Завершает вход для пользователей с включённой 2FA. Принимает временный twoFactorToken "
                    + "(полученный при логине) и 6-значный код из приложения-аутентификатора.")
    @ApiResponse(responseCode = "200", description = "Вход подтверждён, токены выданы", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))
    })
    @ApiResponse(responseCode = "401", description = "Неверный код 2FA или истёкший токен", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping("/2fa/verify")
    public ResponseEntity<LoginResponseDto> verifyTwoFactor(
            @RequestBody @Valid TwoFactorVerifyLoginDto dto) {
        return ResponseEntity.ok(authService.verifyTwoFactor(dto));
    }

    @Operation(summary = "Начало настройки 2FA",
            description = "Генерирует секретный ключ TOTP и возвращает его вместе с URI для QR-кода. "
                    + "Пользователь должен отсканировать QR-код в приложении-аутентификаторе (Google Authenticator, Authy и др.), "
                    + "а затем подтвердить настройку через /api/auth/2fa/confirm.")
    @ApiResponse(responseCode = "200", description = "Секрет сгенерирован", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = TwoFactorSetupResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "2FA уже включена", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/2fa/setup")
    public ResponseEntity<TwoFactorSetupResponseDto> setup2fa(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(authService.setup2fa(userDetails.getId()));
    }

    @Operation(summary = "Подтверждение настройки 2FA",
            description = "Пользователь вводит код из приложения-аутентификатора для подтверждения корректной настройки. "
                    + "После успешной проверки 2FA будет включена для аккаунта.")
    @ApiResponse(responseCode = "204", description = "2FA успешно включена")
    @ApiResponse(responseCode = "401", description = "Неверный код 2FA", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/2fa/confirm")
    public ResponseEntity<Void> confirm2fa(
            @RequestBody @Valid TwoFactorCodeDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        authService.confirm2fa(userDetails.getId(), dto.code());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Отключение 2FA",
            description = "Требует текущий пароль и действующий код из приложения-аутентификатора для подтверждения.")
    @ApiResponse(responseCode = "204", description = "2FA отключена")
    @ApiResponse(responseCode = "401", description = "Неверный пароль или код 2FA", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/2fa/disable")
    public ResponseEntity<Void> disable2fa(
            @RequestBody @Valid TwoFactorDisableDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        authService.disable2fa(userDetails.getId(), dto);
        return ResponseEntity.noContent().build();
    }
}
