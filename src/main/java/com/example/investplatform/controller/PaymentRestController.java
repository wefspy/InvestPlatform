package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.payment.DepositRequestDto;
import com.example.investplatform.application.dto.payment.PaymentResponseDto;
import com.example.investplatform.application.service.PaymentService;
import com.example.investplatform.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Платежи", description = "Пополнение лицевого счёта через ЮKassa и получение статусов платежей")
public class PaymentRestController {

    private final PaymentService paymentService;

    @Operation(summary = "Создание платежа на пополнение лицевого счёта через ЮKassa",
            description = "Создаёт платёж и возвращает confirmation_url — на него нужно перенаправить клиента для оплаты.")
    @ApiResponse(responseCode = "200", description = "Платёж создан", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "502", description = "Ошибка взаимодействия с ЮKassa", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('INVESTOR', 'EMITENT')")
    public ResponseEntity<PaymentResponseDto> deposit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody DepositRequestDto request) {
        PaymentResponseDto response = paymentService.initDeposit(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение платежа по ID")
    @ApiResponse(responseCode = "200", description = "Платёж найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponseDto.class))
    })
    @ApiResponse(responseCode = "404", description = "Платёж не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INVESTOR', 'EMITENT', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<PaymentResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }
}
