package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.CreateOperatorDto;
import com.example.investplatform.application.dto.OperatorResponseDto;
import com.example.investplatform.application.dto.UpdateOperatorDto;
import com.example.investplatform.application.service.OperatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OperatorRestController {

    private final OperatorService operatorService;

    @Operation(summary = "Создание оператора (только для администратора)")
    @ApiResponse(responseCode = "201", description = "Оператор успешно создан", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = OperatorResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OperatorResponseDto> create(@RequestBody @Valid CreateOperatorDto dto) {
        OperatorResponseDto response = operatorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Получение данных оператора по ID (только для администратора)")
    @ApiResponse(responseCode = "200", description = "Данные оператора", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = OperatorResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Оператор не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OperatorResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(operatorService.getById(id));
    }

    @Operation(summary = "Редактирование данных оператора (только для администратора)",
            description = "Обновляет ФИО оператора по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Данные обновлены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = OperatorResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Оператор не найден или некорректные параметры запроса", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OperatorResponseDto> update(@PathVariable Long id,
                                                      @RequestBody @Valid UpdateOperatorDto dto) {
        return ResponseEntity.ok(operatorService.update(id, dto));
    }
}
