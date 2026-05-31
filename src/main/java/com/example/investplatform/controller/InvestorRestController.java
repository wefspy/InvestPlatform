package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.investor.CreateInvestorIndividualDto;
import com.example.investplatform.application.dto.investor.CreateInvestorLegalEntityDto;
import com.example.investplatform.application.dto.investor.CreateInvestorPrivateEntrepreneurDto;
import com.example.investplatform.application.dto.investor.InvestorDocumentResponseDto;
import com.example.investplatform.application.dto.investor.InvestorResponseDto;
import com.example.investplatform.application.dto.investor.UpdateInvestorIndividualDto;
import com.example.investplatform.application.dto.investor.UpdateInvestorLegalEntityDto;
import com.example.investplatform.application.dto.investor.UpdateInvestorPrivateEntrepreneurDto;
import com.example.investplatform.application.service.InvestorService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investors")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InvestorRestController {

    private final InvestorService investorService;

    @Operation(summary = "Регистрация инвестора — физическое лицо",
            description = "Требуемые документы: passport_main (паспорт — основная страница), "
                    + "passport_registration (паспорт — страница регистрации)")
    @ApiResponse(responseCode = "201", description = "Инвестор успешно создан", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestorResponseDto.class))
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
    @PostMapping(value = "/individual", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvestorResponseDto> createIndividual(
            @RequestPart("data") @Valid CreateInvestorIndividualDto dto,
            @RequestPart("passport_main") MultipartFile passportMain,
            @RequestPart("passport_registration") MultipartFile passportRegistration) {

        Map<String, MultipartFile> documents = new LinkedHashMap<>();
        documents.put("passport_main", passportMain);
        documents.put("passport_registration", passportRegistration);

        InvestorResponseDto response = investorService.createIndividual(dto, documents);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Регистрация инвестора — индивидуальный предприниматель",
            description = "Требуемые документы: passport_main, passport_registration, "
                    + "ip_registration (свидетельство о регистрации ИП)")
    @ApiResponse(responseCode = "201", description = "Инвестор успешно создан", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestorResponseDto.class))
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
    public ResponseEntity<InvestorResponseDto> createPrivateEntrepreneur(
            @RequestPart("data") @Valid CreateInvestorPrivateEntrepreneurDto dto,
            @RequestPart("passport_main") MultipartFile passportMain,
            @RequestPart("passport_registration") MultipartFile passportRegistration,
            @RequestPart("ip_registration") MultipartFile ipRegistration) {

        Map<String, MultipartFile> documents = new LinkedHashMap<>();
        documents.put("passport_main", passportMain);
        documents.put("passport_registration", passportRegistration);
        documents.put("ip_registration", ipRegistration);

        InvestorResponseDto response = investorService.createPrivateEntrepreneur(dto, documents);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Регистрация инвестора — юридическое лицо",
            description = "Требуемые документы: le_registration (свидетельство о регистрации ЮЛ), "
                    + "le_charter (устав), le_executive_decision (решение об избрании ЕИО)")
    @ApiResponse(responseCode = "201", description = "Инвестор успешно создан", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestorResponseDto.class))
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
    public ResponseEntity<InvestorResponseDto> createLegalEntity(
            @RequestPart("data") @Valid CreateInvestorLegalEntityDto dto,
            @RequestPart("le_registration") MultipartFile leRegistration,
            @RequestPart("le_charter") MultipartFile leCharter,
            @RequestPart("le_executive_decision") MultipartFile leExecutiveDecision) {

        Map<String, MultipartFile> documents = new LinkedHashMap<>();
        documents.put("le_registration", leRegistration);
        documents.put("le_charter", leCharter);
        documents.put("le_executive_decision", leExecutiveDecision);

        InvestorResponseDto response = investorService.createLegalEntity(dto, documents);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ========================= РЕДАКТИРОВАНИЕ ДАННЫХ =========================

    @Operation(summary = "Редактирование данных инвестора — физическое лицо (администратор)",
            description = "Обновляет анкетные данные инвестора-ФЛ по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Данные обновлены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestorResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Инвестор не найден или не является ФЛ, либо некорректные данные", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PutMapping("/individual/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvestorResponseDto> updateIndividual(
            @PathVariable Long id,
            @RequestBody @Valid UpdateInvestorIndividualDto dto) {
        return ResponseEntity.ok(investorService.updateIndividual(id, dto));
    }

    @Operation(summary = "Редактирование данных инвестора — индивидуальный предприниматель (администратор)",
            description = "Обновляет анкетные данные инвестора-ИП по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Данные обновлены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestorResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Инвестор не найден или не является ИП, либо некорректные данные", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PutMapping("/private-entrepreneur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvestorResponseDto> updatePrivateEntrepreneur(
            @PathVariable Long id,
            @RequestBody @Valid UpdateInvestorPrivateEntrepreneurDto dto) {
        return ResponseEntity.ok(investorService.updatePrivateEntrepreneur(id, dto));
    }

    @Operation(summary = "Редактирование данных инвестора — юридическое лицо (администратор)",
            description = "Обновляет анкетные данные инвестора-ЮЛ по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Данные обновлены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestorResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Инвестор не найден или не является ЮЛ, либо некорректные данные", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PutMapping("/legal-entity/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvestorResponseDto> updateLegalEntity(
            @PathVariable Long id,
            @RequestBody @Valid UpdateInvestorLegalEntityDto dto) {
        return ResponseEntity.ok(investorService.updateLegalEntity(id, dto));
    }

    // ========================= ДОКУМЕНТЫ =========================

    @Operation(summary = "Получение списка документов инвестора")
    @ApiResponse(responseCode = "200", description = "Список документов")
    @ApiResponse(responseCode = "400", description = "Инвестор не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<List<InvestorDocumentResponseDto>> getDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(investorService.getDocuments(id));
    }

    @Operation(summary = "Добавление нового документа инвестора",
            description = "Загружает новый документ указанного типа. Код типа передаётся в поле `type` "
                    + "(например, passport_main, passport_registration, ip_registration, le_charter).")
    @ApiResponse(responseCode = "201", description = "Документ добавлен", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestorDocumentResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Инвестор не найден или неизвестный тип документа", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvestorDocumentResponseDto> addDocument(
            @PathVariable Long id,
            @RequestParam("type") String type,
            @RequestPart("file") MultipartFile file) {
        InvestorDocumentResponseDto response = investorService.addDocument(id, type, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Удаление документа инвестора")
    @ApiResponse(responseCode = "204", description = "Документ удалён")
    @ApiResponse(responseCode = "400", description = "Инвестор или документ не найдены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @DeleteMapping("/{id}/documents/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id,
                                                @PathVariable Long documentId) {
        investorService.deleteDocument(id, documentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Замена докум��нта инвестора",
            description = "Заменяет файл существующего документа. Тип документа остаётся прежним.")
    @ApiResponse(responseCode = "200", description = "Документ заменён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestorDocumentResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Инвестор или документ не найдены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PutMapping(value = "/{id}/documents/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvestorDocumentResponseDto> replaceDocument(
            @PathVariable Long id,
            @PathVariable Long documentId,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(investorService.replaceDocument(id, documentId, file));
    }
}
