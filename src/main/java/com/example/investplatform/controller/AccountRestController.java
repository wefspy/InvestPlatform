package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.account.AccountBalanceDto;
import com.example.investplatform.application.dto.account.AccountTransactionDto;
import com.example.investplatform.application.service.AccountService;
import com.example.investplatform.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AccountRestController {

    private final AccountService accountService;

    @Operation(summary = "Получение баланса лицевого счёта текущего пользователя")
    @ApiResponse(responseCode = "200", description = "Баланс успешно получен", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AccountBalanceDto.class))
    })
    @ApiResponse(responseCode = "404", description = "Лицевой счёт не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('INVESTOR', 'EMITENT')")
    public ResponseEntity<AccountBalanceDto> getMyBalance(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        AccountBalanceDto balance = accountService.getBalance(userDetails.getId());
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "История операций по лицевому счёту текущего пользователя",
            description = "Возвращает список операций с пагинацией. По умолчанию сортировка по дате (новые первые).")
    @ApiResponse(responseCode = "200", description = "Список операций")
    @ApiResponse(responseCode = "404", description = "Лицевой счёт не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('INVESTOR', 'EMITENT')")
    public ResponseEntity<Page<AccountTransactionDto>> getMyTransactions(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AccountTransactionDto> transactions = accountService.getTransactions(userDetails.getId(), pageable);
        return ResponseEntity.ok(transactions);
    }
}
