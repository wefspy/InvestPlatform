package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.proposal.*;
import com.example.investplatform.application.service.InvestmentProposalService;
import com.example.investplatform.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InvestmentProposalRestController {

    private final InvestmentProposalService proposalService;

    // ========================= ЭМИТЕНТ =========================

    @Operation(summary = "Создание инвестиционного предложения",
            description = "Эмитент создаёт ИП от своего имени. Создаётся в статусе 'Черновик'.")
    @ApiResponse(responseCode = "201", description = "ИП успешно создано", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentProposalResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping
    @PreAuthorize("hasRole('EMITENT')")
    public ResponseEntity<InvestmentProposalResponseDto> create(
            @RequestBody @Valid CreateInvestmentProposalDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        InvestmentProposalResponseDto response = proposalService.create(dto, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Обновление инвестиционного предложения",
            description = "Эмитент редактирует своё ИП. Возможно только в статусе 'Черновик'.")
    @ApiResponse(responseCode = "200", description = "ИП успешно обновлено", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentProposalResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Некорректные параметры или недопустимый статус", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "404", description = "ИП не найдено", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMITENT')")
    public ResponseEntity<InvestmentProposalResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateInvestmentProposalDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        InvestmentProposalResponseDto response = proposalService.update(id, dto, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Мои инвестиционные предложения",
            description = "Эмитент получает список только своих ИП.")
    @ApiResponse(responseCode = "200", description = "Список ИП эмитента")
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMITENT')")
    public ResponseEntity<Page<InvestmentProposalListItemDto>> getMyProposals(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(proposalService.getMyProposals(userDetails.getId(), pageable));
    }

    @Operation(summary = "Изменение статуса ИП эмитентом",
            description = "Эмитент может: draft→pending (отправить на модерацию), rejected→draft (вернуть на доработку).")
    @ApiResponse(responseCode = "200", description = "Статус ИП изменён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentProposalResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Недопустимый переход статуса", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMITENT')")
    public ResponseEntity<InvestmentProposalResponseDto> changeStatusByEmitent(
            @PathVariable Long id,
            @RequestBody @Valid ChangeProposalStatusDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(proposalService.changeStatusByEmitent(id, dto, userDetails.getId()));
    }

    @Operation(summary = "Загрузка документа к ИП",
            description = "Эмитент загружает документ. Возможно только в статусе 'Черновик'. "
                    + "Коды типов: financial_report, audit_conclusion, issue_decision, placement_conditions, "
                    + "securities_prospectus, draft_contract, risk_warning, other.")
    @ApiResponse(responseCode = "201", description = "Документ загружен")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры или недопустимый статус", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EMITENT')")
    public ResponseEntity<Void> uploadDocument(
            @PathVariable Long id,
            @RequestParam("documentType") String documentTypeCode,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        proposalService.uploadDocument(id, documentTypeCode, file, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Получение списка документов ИП",
            description = "Доступно эмитенту (свои ИП), оператору и администратору.")
    @ApiResponse(responseCode = "200", description = "Список документов ИП")
    @ApiResponse(responseCode = "404", description = "ИП не найдено", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'EMITENT', 'INVESTOR')")
    public ResponseEntity<List<ProposalDocumentResponseDto>> getDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(proposalService.getDocuments(id));
    }

    // ========================= ОПЕРАТОР (модерация) =========================

    @Operation(summary = "Пул ИП на модерацию",
            description = "Общий список ИП в статусе 'pending', ещё не захваченных ни одним оператором. "
                    + "Отсортирован по дате отправки (FIFO).")
    @ApiResponse(responseCode = "200", description = "Список доступных для модерации ИП")
    @GetMapping("/moderation")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Page<InvestmentProposalListItemDto>> getForModeration(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(proposalService.getForModeration(pageable));
    }

    @Operation(summary = "Мои ИП на рассмотрении",
            description = "Список ИП, закреплённых за текущим оператором (в статусе 'reviewing').")
    @ApiResponse(responseCode = "200", description = "Список ИП оператора")
    @GetMapping("/moderation/my")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Page<InvestmentProposalListItemDto>> getMyReviewing(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(proposalService.getMyReviewing(userDetails.getId(), pageable));
    }

    @Operation(summary = "Забрать ИП на рассмотрение",
            description = "Атомарная операция: ИП переходит pending→reviewing и закрепляется за оператором. "
                    + "Если два оператора одновременно вызовут claim, только один получит ИП (второй — 409 Conflict). "
                    + "После захвата оператор обязан отправлять heartbeat каждые 5 минут, иначе ИП вернётся в пул.")
    @ApiResponse(responseCode = "200", description = "ИП закреплено за оператором", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentProposalResponseDto.class))
    })
    @ApiResponse(responseCode = "409", description = "ИП уже забрано другим оператором", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping("/{id}/claim")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<InvestmentProposalResponseDto> claim(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(proposalService.claimForReview(id, userDetails.getId()));
    }

    @Operation(summary = "Heartbeat — подтверждение активности оператора",
            description = "Оператор вызывает каждые 5 минут, подтверждая что работает над ИП. "
                    + "Если heartbeat не обновляется >5 минут, ИП автоматически открепляется и возвращается в пул.")
    @ApiResponse(responseCode = "204", description = "Heartbeat обновлён")
    @ApiResponse(responseCode = "400", description = "ИП не закреплено за вами", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PatchMapping("/{id}/heartbeat")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Void> heartbeat(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        proposalService.heartbeat(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Принятие решения по ИП",
            description = "Оператор, за которым закреплено ИП, принимает решение: "
                    + "reviewing→active (одобрить) или reviewing→rejected (отклонить с комментарием). "
                    + "После решения блокировка снимается.")
    @ApiResponse(responseCode = "200", description = "Решение принято", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentProposalResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Недопустимый переход или ИП не закреплено за вами", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<InvestmentProposalResponseDto> resolve(
            @PathVariable Long id,
            @RequestBody @Valid ChangeProposalStatusDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(proposalService.resolveReview(id, dto, userDetails.getId()));
    }

    // ========================= ОБЩИЕ =========================

    @Operation(summary = "Получение инвестиционного предложения по ID",
            description = "Доступно всем авторизованным пользователям.")
    @ApiResponse(responseCode = "200", description = "ИП найдено", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentProposalResponseDto.class))
    })
    @ApiResponse(responseCode = "404", description = "ИП не найдено", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'EMITENT', 'INVESTOR')")
    public ResponseEntity<InvestmentProposalResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(proposalService.getById(id));
    }

    @Operation(summary = "Список активных ИП",
            description = "Возвращает ИП в статусе 'active', доступные для инвестирования. Доступно всем авторизованным.")
    @ApiResponse(responseCode = "200", description = "Список активных ИП")
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'EMITENT', 'INVESTOR')")
    public ResponseEntity<Page<InvestmentProposalListItemDto>> getActive(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(proposalService.getActive(pageable));
    }

    // ========================= АДМИНИСТРАТОР =========================

    @Operation(summary = "Список всех ИП (администратор)",
            description = "Администратор видит все ИП с пагинацией.")
    @ApiResponse(responseCode = "200", description = "Список всех ИП")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InvestmentProposalListItemDto>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(proposalService.getAll(pageable));
    }

    @Operation(summary = "Список ИП по статусу (администратор)",
            description = "Администратор фильтрует ИП по коду статуса: draft, pending, reviewing, rejected, active, failed, completed.")
    @ApiResponse(responseCode = "200", description = "Список ИП по статусу")
    @GetMapping("/all/by-status/{statusCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InvestmentProposalListItemDto>> getByStatus(
            @PathVariable String statusCode,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(proposalService.getByStatus(statusCode, pageable));
    }

    @Operation(summary = "Изменение статуса ИП администратором",
            description = "Администратор может выполнять любые допустимые переходы статуса. Снимает блокировку оператора.")
    @ApiResponse(responseCode = "200", description = "Статус ИП изменён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentProposalResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Недопустимый переход статуса", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PatchMapping("/{id}/admin-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvestmentProposalResponseDto> changeStatusByAdmin(
            @PathVariable Long id,
            @RequestBody @Valid ChangeProposalStatusDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(proposalService.changeStatusByAdmin(id, dto, userDetails.getId()));
    }
}
