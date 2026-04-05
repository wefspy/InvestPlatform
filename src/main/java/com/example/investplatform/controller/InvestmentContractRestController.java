package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.contract.*;
import com.example.investplatform.application.dto.proposal.ChangeProposalStatusDto;
import com.example.investplatform.application.service.InvestmentContractService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InvestmentContractRestController {

    private final InvestmentContractService contractService;
    private final com.example.investplatform.application.service.CommissionService commissionService;

    // ========================= ИНВЕСТОР =========================

    @Operation(summary = "Параметры комиссии платформы",
            description = "Возвращает текущую ставку комиссии (про��ент), минимальную и максимальную сумму.")
    @ApiResponse(responseCode = "200", description = "Па��аметры комиссии", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = CommissionInfoDto.class))
    })
    @GetMapping("/commission-info")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<CommissionInfoDto> getCommissionInfo() {
        return ResponseEntity.ok(commissionService.getInfo());
    }

    @Operation(summary = "Предварительный расчёт стоимости договора",
            description = "Возвращает расчёт суммы инвестиции, комиссии платформы и итого "
                    + "для указанного количества ценных бумаг по конкретному ИП. Не списывает средства.")
    @ApiResponse(responseCode = "200", description = "Расчёт выполнен", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ContractCalculationDto.class))
    })
    @GetMapping("/calculate")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<ContractCalculationDto> calculate(
            @RequestParam Long proposalId,
            @RequestParam Long securitiesQuantity) {
        return ResponseEntity.ok(contractService.calculate(proposalId, securitiesQuantity));
    }

    @Operation(summary = "Создание договора инвестирования",
            description = "Инвестор создаёт ДИ по активному ИП. Средства списываются со счёта сразу, "
                    + "договор отправляется на модерацию оператору. Один инвестор — один договор на одно ИП.")
    @ApiResponse(responseCode = "201", description = "Договор создан, средства списаны", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentContractResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Некорректные параметры (ИП не активно, сумма вне диапазона, дубликат)", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "402", description = "Недостаточно средств на счёте", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<InvestmentContractResponseDto> create(
            @RequestBody @Valid CreateInvestmentContractDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        InvestmentContractResponseDto response = contractService.create(dto, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Отзыв договора инвестирования",
            description = "Инвестор может отозвать договор в течение 5 дней с момента создания, "
                    + "но не позднее срока действия ИП. Средства возвращаются на счёт.")
    @ApiResponse(responseCode = "200", description = "Договор отозван, средства возвращены", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentContractResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Отзыв невозможен (срок истёк или недопустимый статус)", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<InvestmentContractResponseDto> withdraw(
            @PathVariable Long id,
            @RequestBody @Valid WithdrawContractDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(contractService.withdraw(id, dto, userDetails.getId()));
    }

    @Operation(summary = "Мои договоры инвестирования",
            description = "Инвестор получает список своих ДИ с пагинацией.")
    @ApiResponse(responseCode = "200", description = "Список договоров")
    @GetMapping("/my")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<Page<InvestmentContractListItemDto>> getMyContracts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(contractService.getMyContracts(userDetails.getId(), pageable));
    }

    @Operation(summary = "Получение договора по ID",
            description = "Доступно инвестору (свой), оператору и администратору.")
    @ApiResponse(responseCode = "200", description = "Договор найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentContractResponseDto.class))
    })
    @ApiResponse(responseCode = "404", description = "Договор не найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'INVESTOR')")
    public ResponseEntity<InvestmentContractResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getById(id));
    }

    // ========================= ОПЕРАТОР (модерация) =========================

    @Operation(summary = "Общий пул договоров на модерацию",
            description = "Оператор видит все ДИ в статусе 'reviewing', не закреплённые за другим оператором.")
    @ApiResponse(responseCode = "200", description = "Список договоров на модерацию")
    @GetMapping("/moderation")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Page<InvestmentContractListItemDto>> getForModeration(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(contractService.getForModeration(pageable));
    }

    @Operation(summary = "Мои договоры на рассмотрении",
            description = "Договоры, закреплённые за текущим оператором.")
    @ApiResponse(responseCode = "200", description = "Список закреплённых договоров")
    @GetMapping("/moderation/my")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Page<InvestmentContractListItemDto>> getMyReviewing(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(contractService.getMyReviewing(userDetails.getId(), pageable));
    }

    @Operation(summary = "Забрать договор на рассмотрение",
            description = "Атомарный захват ДИ оператором. Договор закрепляется за оператором "
                    + "и убирается из общего пула. Требуется отправка heartbeat каждые 5 минут.")
    @ApiResponse(responseCode = "200", description = "Договор закреплён за оператором", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentContractResponseDto.class))
    })
    @ApiResponse(responseCode = "409", description = "Договор уже забран другим оператором", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PostMapping("/{id}/claim")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<InvestmentContractResponseDto> claimForReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(contractService.claimForReview(id, userDetails.getId()));
    }

    @Operation(summary = "Heartbeat — подтверждение активности оператора",
            description = "Оператор отправляет каждые 5 минут для сохранения закрепления. "
                    + "Если heartbeat не поступает более 5 минут, договор открепляется и возвращается в общий пул.")
    @ApiResponse(responseCode = "204", description = "Heartbeat принят")
    @ApiResponse(responseCode = "400", description = "Договор не закреплён за вами", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PatchMapping("/{id}/heartbeat")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Void> heartbeat(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        contractService.heartbeat(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Модерация договора оператором",
            description = "Оператор одобряет (approved) или отклоняет (rejected) ДИ. "
                    + "Только оператор, за которым закреплён договор, может вынести решение. "
                    + "При отклонении средства возвращаются инвестору.")
    @ApiResponse(responseCode = "200", description = "Решение принято", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentContractResponseDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Недопустимый переход статуса", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @PatchMapping("/{id}/moderate")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<InvestmentContractResponseDto> moderate(
            @PathVariable Long id,
            @RequestBody @Valid ChangeProposalStatusDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(contractService.resolveReview(id, dto, userDetails.getId()));
    }
}
