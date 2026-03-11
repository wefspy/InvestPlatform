package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.emitent.CreateEmitentLegalEntityDto;
import com.example.investplatform.application.dto.emitent.CreateEmitentPrivateEntrepreneurDto;
import com.example.investplatform.application.dto.emitent.EmitentResponseDto;
import com.example.investplatform.application.service.EmitentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/emitents")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmitentRestController {

    private final EmitentService emitentService;

    @Operation(summary = "Регистрация эмитента — индивидуальный предприниматель",
            description = "Требуемые документы: financial_report (годовая бухгалтерская отчётность), "
                    + "charter (устав)")
    @ApiResponse(responseCode = "201", description = "Эмитент успешно создан", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EmitentResponseDto.class))
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
    @PostMapping(value = "/private-entrepreneur", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmitentResponseDto> createPrivateEntrepreneur(
            @RequestPart("data") @Valid CreateEmitentPrivateEntrepreneurDto dto,
            @RequestPart("financial_report") MultipartFile financialReport,
            @RequestPart("charter") MultipartFile charter) {

        Map<String, MultipartFile> documents = new LinkedHashMap<>();
        documents.put("financial_report", financialReport);
        documents.put("charter", charter);

        EmitentResponseDto response = emitentService.createPrivateEntrepreneur(dto, documents);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Регистрация эмитента — юридическое лицо",
            description = "Требуемые документы: financial_report (годовая бухгалтерская отчётность), "
                    + "charter (устав), egrul_extract (выписка из ЕГРЮЛ)")
    @ApiResponse(responseCode = "201", description = "Эмитент успешно создан", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EmitentResponseDto.class))
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
    @PostMapping(value = "/legal-entity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmitentResponseDto> createLegalEntity(
            @RequestPart("data") @Valid CreateEmitentLegalEntityDto dto,
            @RequestPart("financial_report") MultipartFile financialReport,
            @RequestPart("charter") MultipartFile charter,
            @RequestPart("egrul_extract") MultipartFile egrulExtract) {

        Map<String, MultipartFile> documents = new LinkedHashMap<>();
        documents.put("financial_report", financialReport);
        documents.put("charter", charter);
        documents.put("egrul_extract", egrulExtract);

        EmitentResponseDto response = emitentService.createLegalEntity(dto, documents);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
