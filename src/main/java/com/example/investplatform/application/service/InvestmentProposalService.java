package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.proposal.*;
import com.example.investplatform.application.exception.InvestmentProposalNotFoundException;
import com.example.investplatform.application.exception.InvalidProposalStatusTransitionException;
import com.example.investplatform.application.exception.ProposalAlreadyClaimedException;
import com.example.investplatform.infrastructure.repository.*;
import com.example.investplatform.model.entity.emitent.Emitent;
import com.example.investplatform.model.entity.proposal.*;
import com.example.investplatform.model.entity.user.Operator;
import com.example.investplatform.model.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InvestmentProposalService {

    private static final String DRAFT_STATUS = "draft";
    private static final String PENDING_STATUS = "pending";
    private static final String REVIEWING_STATUS = "reviewing";
    private static final String REJECTED_STATUS = "rejected";
    private static final String ACTIVE_STATUS = "active";

    private static final Map<String, Set<String>> EMITENT_TRANSITIONS = Map.of(
            DRAFT_STATUS, Set.of(PENDING_STATUS),
            REJECTED_STATUS, Set.of(DRAFT_STATUS)
    );

    private final InvestmentProposalRepository proposalRepository;
    private final ProposalStatusRepository statusRepository;
    private final InvestmentMethodRepository investmentMethodRepository;
    private final ProposalStatusHistoryRepository statusHistoryRepository;
    private final ProposalDocumentRepository documentRepository;
    private final ProposalDocumentTypeRepository documentTypeRepository;
    private final EmitentRepository emitentRepository;
    private final OperatorRepository operatorRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // ========================= Эмитент =========================

    @Transactional
    public InvestmentProposalResponseDto create(CreateInvestmentProposalDto dto, Long emitentUserId) {
        Emitent emitent = emitentRepository.findByUserId(emitentUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Эмитент для пользователя с ID %d не найден".formatted(emitentUserId)));

        InvestmentMethod method = investmentMethodRepository.findByCode(dto.investmentMethodCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Способ инвестирования '%s' не найден".formatted(dto.investmentMethodCode())));

        ProposalStatus draftStatus = statusRepository.findByCode(DRAFT_STATUS)
                .orElseThrow(() -> new IllegalStateException("Статус 'draft' не найден"));

        InvestmentProposal proposal = InvestmentProposal.builder()
                .emitent(emitent)
                .status(draftStatus)
                .investmentMethod(method)
                .title(dto.title())
                .investmentGoals(dto.investmentGoals())
                .goalRiskFactors(dto.goalRiskFactors())
                .emitentRisks(dto.emitentRisks())
                .investmentRisks(dto.investmentRisks())
                .issueDecisionInfo(dto.issueDecisionInfo())
                .placementProcedure(dto.placementProcedure())
                .placementTerms(dto.placementTerms())
                .placementConditions(dto.placementConditions())
                .hasPreemptiveRight(dto.hasPreemptiveRight())
                .preemptiveRightDetails(dto.preemptiveRightDetails())
                .riskWarning(dto.riskWarning())
                .maxInvestmentAmount(dto.maxInvestmentAmount())
                .minInvestmentAmount(dto.minInvestmentAmount())
                .pricePerUnit(dto.pricePerUnit())
                .totalQuantity(dto.totalQuantity())
                .minPurchaseQuantity(dto.minPurchaseQuantity())
                .maxPurchaseQuantity(dto.maxPurchaseQuantity())
                .proposalStartDate(dto.proposalStartDate())
                .proposalEndDate(dto.proposalEndDate())
                .essentialContractTerms(dto.essentialContractTerms())
                .expertMonitoringInfo(dto.expertMonitoringInfo())
                .hasPropertyRightsCondition(dto.hasPropertyRightsCondition())
                .propertyRightsDetails(dto.propertyRightsDetails())
                .applicableLaw(dto.applicableLaw() != null ? dto.applicableLaw() : "Российская Федерация")
                .suspensiveConditions(dto.suspensiveConditions())
                .collectedAmount(BigDecimal.ZERO)
                .build();

        proposal = proposalRepository.save(proposal);
        saveStatusHistory(proposal, null, draftStatus, emitent.getUser(), null);

        return toResponseDto(proposal);
    }

    @Transactional
    public InvestmentProposalResponseDto update(Long id, UpdateInvestmentProposalDto dto, Long emitentUserId) {
        InvestmentProposal proposal = findProposalOrThrow(id);
        verifyOwnership(proposal, emitentUserId);

        if (!DRAFT_STATUS.equals(proposal.getStatus().getCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Редактирование возможно только в статусе 'Черновик'. Текущий статус: '%s'"
                            .formatted(proposal.getStatus().getName()));
        }

        if (dto.title() != null) proposal.setTitle(dto.title());
        if (dto.investmentGoals() != null) proposal.setInvestmentGoals(dto.investmentGoals());
        if (dto.goalRiskFactors() != null) proposal.setGoalRiskFactors(dto.goalRiskFactors());
        if (dto.emitentRisks() != null) proposal.setEmitentRisks(dto.emitentRisks());
        if (dto.investmentRisks() != null) proposal.setInvestmentRisks(dto.investmentRisks());
        if (dto.issueDecisionInfo() != null) proposal.setIssueDecisionInfo(dto.issueDecisionInfo());
        if (dto.placementProcedure() != null) proposal.setPlacementProcedure(dto.placementProcedure());
        if (dto.placementTerms() != null) proposal.setPlacementTerms(dto.placementTerms());
        if (dto.placementConditions() != null) proposal.setPlacementConditions(dto.placementConditions());
        if (dto.hasPreemptiveRight() != null) proposal.setHasPreemptiveRight(dto.hasPreemptiveRight());
        if (dto.preemptiveRightDetails() != null) proposal.setPreemptiveRightDetails(dto.preemptiveRightDetails());
        if (dto.riskWarning() != null) proposal.setRiskWarning(dto.riskWarning());
        if (dto.maxInvestmentAmount() != null) proposal.setMaxInvestmentAmount(dto.maxInvestmentAmount());
        if (dto.minInvestmentAmount() != null) proposal.setMinInvestmentAmount(dto.minInvestmentAmount());
        if (dto.pricePerUnit() != null) proposal.setPricePerUnit(dto.pricePerUnit());
        if (dto.totalQuantity() != null) proposal.setTotalQuantity(dto.totalQuantity());
        if (dto.minPurchaseQuantity() != null) proposal.setMinPurchaseQuantity(dto.minPurchaseQuantity());
        if (dto.maxPurchaseQuantity() != null) proposal.setMaxPurchaseQuantity(dto.maxPurchaseQuantity());
        if (dto.proposalStartDate() != null) proposal.setProposalStartDate(dto.proposalStartDate());
        if (dto.proposalEndDate() != null) proposal.setProposalEndDate(dto.proposalEndDate());
        if (dto.essentialContractTerms() != null) proposal.setEssentialContractTerms(dto.essentialContractTerms());
        if (dto.expertMonitoringInfo() != null) proposal.setExpertMonitoringInfo(dto.expertMonitoringInfo());
        if (dto.hasPropertyRightsCondition() != null) proposal.setHasPropertyRightsCondition(dto.hasPropertyRightsCondition());
        if (dto.propertyRightsDetails() != null) proposal.setPropertyRightsDetails(dto.propertyRightsDetails());
        if (dto.applicableLaw() != null) proposal.setApplicableLaw(dto.applicableLaw());
        if (dto.suspensiveConditions() != null) proposal.setSuspensiveConditions(dto.suspensiveConditions());

        proposal = proposalRepository.save(proposal);
        return toResponseDto(proposal);
    }

    @Transactional
    public InvestmentProposalResponseDto changeStatusByEmitent(Long id, ChangeProposalStatusDto dto,
                                                                Long emitentUserId) {
        InvestmentProposal proposal = findProposalOrThrow(id);
        verifyOwnership(proposal, emitentUserId);

        ProposalStatus oldStatus = proposal.getStatus();
        Set<String> allowed = EMITENT_TRANSITIONS.getOrDefault(oldStatus.getCode(), Set.of());
        if (!allowed.contains(dto.statusCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Переход из статуса '%s' в '%s' невозможен. Допустимые переходы: %s"
                            .formatted(oldStatus.getCode(), dto.statusCode(), allowed));
        }

        ProposalStatus newStatus = statusRepository.findByCode(dto.statusCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Статус '%s' не найден".formatted(dto.statusCode())));

        User changedBy = userRepository.findById(emitentUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с ID %d не найден".formatted(emitentUserId)));

        proposal.setStatus(newStatus);

        if (PENDING_STATUS.equals(dto.statusCode())) {
            proposal.setSubmittedAt(LocalDateTime.now());
        }

        proposal = proposalRepository.save(proposal);
        saveStatusHistory(proposal, oldStatus, newStatus, changedBy, dto.comment());

        return toResponseDto(proposal);
    }

    @Transactional
    public void uploadDocument(Long proposalId, String documentTypeCode, MultipartFile file,
                               Long emitentUserId) {
        InvestmentProposal proposal = findProposalOrThrow(proposalId);
        verifyOwnership(proposal, emitentUserId);

        if (!DRAFT_STATUS.equals(proposal.getStatus().getCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Загрузка документов возможна только в статусе 'Черновик'");
        }

        ProposalDocumentType docType = documentTypeRepository.findByCode(documentTypeCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Тип документа '%s' не найден".formatted(documentTypeCode)));

        String objectKey = "proposals/%d/%s_%s".formatted(
                proposalId, documentTypeCode, file.getOriginalFilename());
        fileStorageService.upload(file, objectKey);

        ProposalDocument document = ProposalDocument.builder()
                .proposal(proposal)
                .documentType(docType)
                .fileName(file.getOriginalFilename())
                .filePath(objectKey)
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .build();
        documentRepository.save(document);
    }

    @Transactional
    public void deleteDocument(Long proposalId, Long documentId, Long emitentUserId) {
        InvestmentProposal proposal = findProposalOrThrow(proposalId);
        verifyOwnership(proposal, emitentUserId);

        if (!DRAFT_STATUS.equals(proposal.getStatus().getCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Удаление документов возможно только в статусе 'Черновик'");
        }

        ProposalDocument document = documentRepository.findByIdAndProposalId(documentId, proposalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Документ с ID %d не найден в ИП с ID %d".formatted(documentId, proposalId)));

        fileStorageService.delete(document.getFilePath());
        documentRepository.delete(document);
    }

    @Transactional
    public ProposalDocumentResponseDto replaceDocument(Long proposalId, Long documentId,
                                                       MultipartFile file, Long emitentUserId) {
        InvestmentProposal proposal = findProposalOrThrow(proposalId);
        verifyOwnership(proposal, emitentUserId);

        if (!DRAFT_STATUS.equals(proposal.getStatus().getCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Замена документов возможна только в статусе 'Черновик'");
        }

        ProposalDocument document = documentRepository.findByIdAndProposalId(documentId, proposalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Документ с ID %d не найден в ИП с ID %d".formatted(documentId, proposalId)));

        fileStorageService.delete(document.getFilePath());

        String objectKey = "proposals/%d/%s_%s".formatted(
                proposalId, document.getDocumentType().getCode(), file.getOriginalFilename());
        fileStorageService.upload(file, objectKey);

        document.setFileName(file.getOriginalFilename());
        document.setFilePath(objectKey);
        document.setFileSize(file.getSize());
        document.setMimeType(file.getContentType());
        document = documentRepository.save(document);

        return new ProposalDocumentResponseDto(
                document.getId(),
                document.getDocumentType().getCode(),
                document.getDocumentType().getName(),
                document.getFileName(),
                document.getFilePath(),
                document.getFileSize(),
                document.getMimeType(),
                document.getUploadedAt());
    }

    @Transactional(readOnly = true)
    public Page<InvestmentProposalListItemDto> getMyProposals(Long emitentUserId, Pageable pageable) {
        return proposalRepository.findByEmitentUserId(emitentUserId, pageable)
                .map(this::toListItemDto);
    }

    // ========================= Оператор (модерация) =========================

    /**
     * Пул ИП, доступных для захвата: pending + не заблокированные.
     */
    @Transactional(readOnly = true)
    public Page<InvestmentProposalListItemDto> getForModeration(Pageable pageable) {
        return proposalRepository.findAvailableForModeration(pageable)
                .map(this::toListItemDto);
    }

    /**
     * ИП, закреплённые за текущим оператором (в работе).
     */
    @Transactional(readOnly = true)
    public Page<InvestmentProposalListItemDto> getMyReviewing(Long operatorUserId, Pageable pageable) {
        return proposalRepository.findLockedByOperator(operatorUserId, pageable)
                .map(this::toListItemDto);
    }

    /**
     * Атомарный захват ИП оператором.
     * Если два оператора одновременно вызовут claim на одно ИП,
     * только один получит updatedRows=1, второй — 0 (и получит 409 Conflict).
     */
    @Transactional
    public InvestmentProposalResponseDto claimForReview(Long proposalId, Long operatorUserId) {
        Operator operator = operatorRepository.findByUserId(operatorUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Оператор для пользователя с ID %d не найден".formatted(operatorUserId)));

        int updatedRows = proposalRepository.claimForReview(proposalId, operator.getId());

        if (updatedRows == 0) {
            throw new ProposalAlreadyClaimedException(
                    "ИП с ID %d уже забрано другим оператором или недоступно для модерации"
                            .formatted(proposalId));
        }

        // Перечитываем из БД после native UPDATE, чтобы получить актуальное состояние
        InvestmentProposal proposal = findProposalOrThrow(proposalId);

        ProposalStatus oldStatus = statusRepository.findByCode(PENDING_STATUS).orElseThrow();
        ProposalStatus newStatus = proposal.getStatus();
        User changedBy = userRepository.findById(operatorUserId).orElseThrow();
        saveStatusHistory(proposal, oldStatus, newStatus, changedBy, "Оператор забрал ИП на рассмотрение");

        return toResponseDto(proposal);
    }

    /**
     * Heartbeat — оператор подтверждает, что всё ещё работает над ИП.
     * Вызывается каждые 5 минут клиентом.
     */
    @Transactional
    public void heartbeat(Long proposalId, Long operatorUserId) {
        Operator operator = operatorRepository.findByUserId(operatorUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Оператор для пользователя с ID %d не найден".formatted(operatorUserId)));

        int updatedRows = proposalRepository.refreshHeartbeat(proposalId, operator.getId());

        if (updatedRows == 0) {
            throw new InvalidProposalStatusTransitionException(
                    "ИП с ID %d не закреплено за вами или уже завершено".formatted(proposalId));
        }
    }

    /**
     * Оператор принимает решение по ИП: reviewing→active или reviewing→rejected.
     * Проверяет, что ИП заблокировано именно этим оператором.
     */
    @Transactional
    public InvestmentProposalResponseDto resolveReview(Long proposalId, ChangeProposalStatusDto dto,
                                                        Long operatorUserId) {
        InvestmentProposal proposal = findProposalOrThrow(proposalId);

        if (!REVIEWING_STATUS.equals(proposal.getStatus().getCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "ИП не в статусе 'На рассмотрении'");
        }

        Operator operator = operatorRepository.findByUserId(operatorUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Оператор для пользователя с ID %d не найден".formatted(operatorUserId)));

        if (proposal.getLockedBy() == null || !proposal.getLockedBy().getId().equals(operator.getId())) {
            throw new InvalidProposalStatusTransitionException(
                    "ИП с ID %d не закреплено за вами".formatted(proposalId));
        }

        if (!Set.of(ACTIVE_STATUS, REJECTED_STATUS).contains(dto.statusCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Оператор может перевести ИП только в 'active' или 'rejected'. Получено: '%s'"
                            .formatted(dto.statusCode()));
        }

        ProposalStatus oldStatus = proposal.getStatus();
        ProposalStatus newStatus = statusRepository.findByCode(dto.statusCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Статус '%s' не найден".formatted(dto.statusCode())));

        User changedBy = userRepository.findById(operatorUserId).orElseThrow();

        proposal.setStatus(newStatus);
        proposal.setReviewedBy(operator);
        proposal.setReviewedAt(LocalDateTime.now());
        proposal.setLockedBy(null);
        proposal.setLockHeartbeatAt(null);

        if (ACTIVE_STATUS.equals(dto.statusCode())) {
            proposal.setActivatedAt(LocalDateTime.now());
        } else {
            proposal.setRejectionReason(dto.comment());
        }

        proposal = proposalRepository.save(proposal);
        saveStatusHistory(proposal, oldStatus, newStatus, changedBy, dto.comment());

        return toResponseDto(proposal);
    }

    /**
     * Вызывается планировщиком — освобождает ИП с просроченными heartbeat (>5 мин).
     */
    @Transactional
    public int releaseExpiredLocks() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(5);
        return proposalRepository.releaseExpiredLocks(expiredBefore);
    }

    // ========================= Администратор =========================

    @Transactional
    public InvestmentProposalResponseDto changeStatusByAdmin(Long id, ChangeProposalStatusDto dto,
                                                              Long adminUserId) {
        InvestmentProposal proposal = findProposalOrThrow(id);

        Map<String, Set<String>> allTransitions = Map.of(
                DRAFT_STATUS, Set.of(PENDING_STATUS),
                PENDING_STATUS, Set.of(REVIEWING_STATUS),
                REVIEWING_STATUS, Set.of(ACTIVE_STATUS, REJECTED_STATUS),
                REJECTED_STATUS, Set.of(DRAFT_STATUS)
        );

        ProposalStatus oldStatus = proposal.getStatus();
        Set<String> allowed = allTransitions.getOrDefault(oldStatus.getCode(), Set.of());
        if (!allowed.contains(dto.statusCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Переход из статуса '%s' в '%s' невозможен. Допустимые переходы: %s"
                            .formatted(oldStatus.getCode(), dto.statusCode(), allowed));
        }

        ProposalStatus newStatus = statusRepository.findByCode(dto.statusCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Статус '%s' не найден".formatted(dto.statusCode())));

        User changedBy = userRepository.findById(adminUserId).orElseThrow();

        proposal.setStatus(newStatus);

        // При форсированной смене статуса админом — очищаем блокировку
        proposal.setLockedBy(null);
        proposal.setLockHeartbeatAt(null);

        switch (dto.statusCode()) {
            case PENDING_STATUS -> proposal.setSubmittedAt(LocalDateTime.now());
            case ACTIVE_STATUS -> proposal.setActivatedAt(LocalDateTime.now());
            case REJECTED_STATUS -> {
                proposal.setRejectionReason(dto.comment());
                proposal.setReviewedAt(LocalDateTime.now());
            }
        }

        proposal = proposalRepository.save(proposal);
        saveStatusHistory(proposal, oldStatus, newStatus, changedBy, dto.comment());

        return toResponseDto(proposal);
    }

    // ========================= Общие =========================

    @Transactional(readOnly = true)
    public InvestmentProposalResponseDto getById(Long id) {
        return toResponseDto(findProposalOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<InvestmentProposalListItemDto> getAll(Pageable pageable) {
        return proposalRepository.findAllWithDetails(pageable)
                .map(this::toListItemDto);
    }

    @Transactional(readOnly = true)
    public Page<InvestmentProposalListItemDto> getByStatus(String statusCode, Pageable pageable) {
        return proposalRepository.findByStatusCode(statusCode, pageable)
                .map(this::toListItemDto);
    }

    @Transactional(readOnly = true)
    public Page<InvestmentProposalListItemDto> getActive(Pageable pageable) {
        return proposalRepository.findByStatusCode(ACTIVE_STATUS, pageable)
                .map(this::toListItemDto);
    }

    @Transactional(readOnly = true)
    public List<ProposalDocumentResponseDto> getDocuments(Long proposalId) {
        findProposalOrThrow(proposalId);
        return documentRepository.findByProposalId(proposalId).stream()
                .map(doc -> new ProposalDocumentResponseDto(
                        doc.getId(),
                        doc.getDocumentType().getCode(),
                        doc.getDocumentType().getName(),
                        doc.getFileName(),
                        doc.getFilePath(),
                        doc.getFileSize(),
                        doc.getMimeType(),
                        doc.getUploadedAt()))
                .toList();
    }

    // ========================= Private =========================

    private void verifyOwnership(InvestmentProposal proposal, Long emitentUserId) {
        if (!proposal.getEmitent().getUser().getId().equals(emitentUserId)) {
            throw new InvalidProposalStatusTransitionException(
                    "Нет доступа к инвестиционному предложению с ID %d".formatted(proposal.getId()));
        }
    }

    private InvestmentProposal findProposalOrThrow(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new InvestmentProposalNotFoundException(
                        "Инвестиционное предложение с ID %d не найдено".formatted(id)));
    }

    private void saveStatusHistory(InvestmentProposal proposal, ProposalStatus oldStatus,
                                   ProposalStatus newStatus, User changedBy, String comment) {
        ProposalStatusHistory history = ProposalStatusHistory.builder()
                .proposal(proposal)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .comment(comment)
                .build();
        statusHistoryRepository.save(history);
    }

    private InvestmentProposalResponseDto toResponseDto(InvestmentProposal p) {
        return new InvestmentProposalResponseDto(
                p.getId(),
                p.getEmitent().getId(),
                p.getStatus().getCode(),
                p.getStatus().getName(),
                p.getInvestmentMethod().getCode(),
                p.getTitle(),
                p.getInvestmentGoals(),
                p.getGoalRiskFactors(),
                p.getEmitentRisks(),
                p.getInvestmentRisks(),
                p.getIssueDecisionInfo(),
                p.getPlacementProcedure(),
                p.getPlacementTerms(),
                p.getPlacementConditions(),
                p.getHasPreemptiveRight(),
                p.getPreemptiveRightDetails(),
                p.getRiskWarning(),
                p.getMaxInvestmentAmount(),
                p.getMinInvestmentAmount(),
                p.getPricePerUnit(),
                p.getTotalQuantity(),
                p.getMinPurchaseQuantity(),
                p.getMaxPurchaseQuantity(),
                p.getProposalStartDate(),
                p.getProposalEndDate(),
                p.getEssentialContractTerms(),
                p.getExpertMonitoringInfo(),
                p.getHasPropertyRightsCondition(),
                p.getPropertyRightsDetails(),
                p.getApplicableLaw(),
                p.getSuspensiveConditions(),
                p.getCollectedAmount(),
                p.getRejectionReason(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getSubmittedAt(),
                p.getReviewedAt(),
                p.getActivatedAt(),
                p.getClosedAt()
        );
    }

    private InvestmentProposalListItemDto toListItemDto(InvestmentProposal p) {
        return new InvestmentProposalListItemDto(
                p.getId(),
                p.getEmitent().getId(),
                p.getStatus().getCode(),
                p.getStatus().getName(),
                p.getTitle(),
                p.getMaxInvestmentAmount(),
                p.getMinInvestmentAmount(),
                p.getPricePerUnit(),
                p.getTotalQuantity(),
                p.getCollectedAmount(),
                p.getProposalStartDate(),
                p.getProposalEndDate(),
                p.getCreatedAt()
        );
    }
}
