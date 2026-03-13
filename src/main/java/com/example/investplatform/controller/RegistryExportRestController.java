package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.service.RegistryExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/registry")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RegistryExportRestController {

    private final RegistryExportService registryExportService;

    @Operation(
            summary = "Выгрузка регистрационного журнала",
            description = """
                    Формирует XML-файл регистрационного журнала регистратора (REGISTRATION_BOOK_EXT)
                    в формате TRF_22_01. Файл содержит записи операций за указанный период.
                    """
    )
    @ApiResponse(responseCode = "200", description = "XML-файл реестра",
            content = @Content(mediaType = "application/xml"))
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorDto.class)))
    @ApiResponse(responseCode = "403", description = "Доступ запрещён",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorDto.class)))
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<byte[]> exportRegistrationBook(
            @Parameter(description = "Дата начала периода (yyyy-MM-dd)", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @Parameter(description = "Дата окончания периода (yyyy-MM-dd)", required = true, example = "2025-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @Parameter(description = "Идентификатор документа", example = "REG-2025-001")
            @RequestParam(defaultValue = "") String idDoc,

            @Parameter(description = "Наименование отправителя", example = "ООО Регистратор")
            @RequestParam(defaultValue = "InvestPlatform") String senderName,

            @Parameter(description = "Идентификатор отправителя", example = "1234567890")
            @RequestParam(defaultValue = "0") String senderId) {

        if (idDoc.isBlank()) {
            idDoc = "REGBOOK-" + dateFrom.format(DateTimeFormatter.BASIC_ISO_DATE)
                    + "-" + dateTo.format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        byte[] xmlBytes = registryExportService.exportRegistrationBook(
                dateFrom, dateTo, idDoc, senderName, senderId);

        String fileName = "REGISTRATION_BOOK_EXT_"
                + dateFrom.format(DateTimeFormatter.BASIC_ISO_DATE) + "_"
                + dateTo.format(DateTimeFormatter.BASIC_ISO_DATE) + ".xml";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(xmlBytes.length)
                .body(xmlBytes);
    }
}
