package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.emitent.CreateEmitentLegalEntityDto;
import com.example.investplatform.application.dto.emitent.CreateEmitentPrivateEntrepreneurDto;
import com.example.investplatform.application.dto.emitent.EmitentDetailDto;
import com.example.investplatform.application.dto.emitent.EmitentDocumentResponseDto;
import com.example.investplatform.application.dto.emitent.EmitentResponseDto;
import com.example.investplatform.application.dto.emitent.UpdateEmitentLegalEntityDto;
import com.example.investplatform.application.dto.emitent.UpdateEmitentPrivateEntrepreneurDto;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
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

    // ========================= ПОЛУЧЕНИЕ ДАННЫХ =========================

    @Operation(summary = "Получение данных эмитента по ID",
            description = "Возвращает анкетные данные эмитента. В зависимости от типа заполнен блок "
                    + "`privateEntrepreneur` (ИП) или `legalEntity` (ЮЛ), второй блок равен null.")
    @ApiResponse(responseCode = "200", description = "Данные эмитента", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EmitentDetailDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Эмитент не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<EmitentDetailDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(emitentService.getById(id));
    }

    // ========================= РЕДАКТИРОВАНИЕ ДАННЫХ =========================

    @Operation(summary = "Редактирование данных эмитента — индивидуальный предприниматель (администратор)",
            description = "Обновляет анкетные данные эмитента-ИП по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Данные обновлены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EmitentResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Эмитент не найден или не является ИП, либо некорректные данные", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PutMapping("/private-entrepreneur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmitentResponseDto> updatePrivateEntrepreneur(
            @PathVariable Long id,
            @RequestBody @Valid UpdateEmitentPrivateEntrepreneurDto dto) {
        return ResponseEntity.ok(emitentService.updatePrivateEntrepreneur(id, dto));
    }

    @Operation(summary = "Редактирование данных эмитента — юридическое лицо (администратор)",
            description = "Обновляет анкетные данные эмитента-ЮЛ по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Данные обновлены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EmitentResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Эмитент не найден или не является ЮЛ, либо некорректные данные", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PutMapping("/legal-entity/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmitentResponseDto> updateLegalEntity(
            @PathVariable Long id,
            @RequestBody @Valid UpdateEmitentLegalEntityDto dto) {
        return ResponseEntity.ok(emitentService.updateLegalEntity(id, dto));
    }

    // ========================= ДОКУМЕНТЫ =========================

    @Operation(summary = "Получение списка документов эмитента")
    @ApiResponse(responseCode = "200", description = "Список документов")
    @ApiResponse(responseCode = "400", description = "Эмит��нт не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<List<EmitentDocumentResponseDto>> getDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(emitentService.getDocuments(id));
    }

    @Operation(summary = "Добавление нового документа эмитента",
            description = "Загружает новый документ указанного типа. Код типа передаётся в поле `type` "
                    + "(например, financial_report, charter, egrul_extract).")
    @ApiResponse(responseCode = "201", description = "Документ добавлен", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EmitentDocumentResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Эмитент не найден или неизвестный тип документа", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmitentDocumentResponseDto> addDocument(
            @PathVariable Long id,
            @RequestParam("type") String type,
            @RequestPart("file") MultipartFile file) {
        EmitentDocumentResponseDto response = emitentService.addDocument(id, type, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Удаление документа эмитента")
    @ApiResponse(responseCode = "204", description = "Документ удалён")
    @ApiResponse(responseCode = "400", description = "Эмитент или документ не найдены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @DeleteMapping("/{id}/documents/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id,
                                                @PathVariable Long documentId) {
        emitentService.deleteDocument(id, documentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Замена документа эмитента",
            description = "Заменяет файл существующего документа. Тип документа остаётся прежним.")
    @ApiResponse(responseCode = "200", description = "Документ заменён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EmitentDocumentResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Эмитент или документ не найдены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PutMapping(value = "/{id}/documents/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmitentDocumentResponseDto> replaceDocument(
            @PathVariable Long id,
            @PathVariable Long documentId,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(emitentService.replaceDocument(id, documentId, file));
    }
}
