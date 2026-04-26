package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.payment.DepositRequestDto;
import com.example.investplatform.application.dto.payment.PaymentHistoryItemDto;
import com.example.investplatform.application.dto.payment.PaymentResponseDto;
import com.example.investplatform.application.dto.payment.PaymentStatusDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
            description = "Создаёт платёж и возвращает confirmation_token — его нужно передать в JS-виджет ЮKassa (YooMoneyCheckoutWidget) на фронтенде.")
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
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<PaymentResponseDto> deposit(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody DepositRequestDto request) {
        PaymentResponseDto response = paymentService.initDeposit(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "История платежей и операций по счёту текущего пользователя",
            description = "Возвращает единый журнал движений по лицевому счёту с пагинацией: " +
                    "пополнения через ЮKassa, заморозку средств при создании договора, разблокировку при отзыве/отклонении, " +
                    "списания по инвестициям, комиссии платформы и поступления эмитенту. " +
                    "Для операций, связанных с платежом ЮKassa, возвращаются также yukassaPaymentId, статус и ссылка на чек. " +
                    "По умолчанию сортировка по дате (новые первые).")
    @ApiResponse(responseCode = "200", description = "Список операций")
    @ApiResponse(responseCode = "404", description = "Лицевой счёт не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('INVESTOR', 'EMITENT')")
    public ResponseEntity<Page<PaymentHistoryItemDto>> getHistory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getHistory(userDetails.getId(), pageable));
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

    @Operation(summary = "Актуальный статус платежа",
            description = "Принудительно перезапрашивает статус у ЮKassa (минуя 30-секундный порог свежести), " +
                    "если платёж ещё не в финальном статусе. Если статус уже succeeded/canceled — отдаёт из кэша. " +
                    "Используется фронтендом для поллинга после закрытия виджета, чтобы убедиться, " +
                    "что backend получил подтверждение от ЮKassa.")
    @ApiResponse(responseCode = "200", description = "Текущий статус платежа", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentStatusDto.class))
    })
    @ApiResponse(responseCode = "404", description = "Платёж не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('INVESTOR', 'EMITENT', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<PaymentStatusDto> getStatus(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getStatus(id));
    }
}
