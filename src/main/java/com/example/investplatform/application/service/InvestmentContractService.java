package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.contract.*;
import com.example.investplatform.application.dto.proposal.ChangeProposalStatusDto;
import com.example.investplatform.application.exception.*;
import com.example.investplatform.infrastructure.repository.*;
import com.example.investplatform.model.entity.account.AccountTransaction;
import com.example.investplatform.model.entity.account.PersonalAccount;
import com.example.investplatform.model.entity.contract.ContractStatus;
import com.example.investplatform.model.entity.contract.ContractStatusHistory;
import com.example.investplatform.model.entity.contract.InvestmentContract;
import com.example.investplatform.model.entity.investor.Investor;
import com.example.investplatform.model.entity.proposal.InvestmentProposal;
import com.example.investplatform.model.entity.user.Operator;
import com.example.investplatform.model.entity.user.User;
import com.example.investplatform.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestmentContractService {

    private static final String REVIEWING_STATUS = "reviewing";
    private static final String WITHDRAWN_STATUS = "withdrawn";
    private static final String REJECTED_STATUS = "rejected";
    private static final String APPROVED_STATUS = "approved";

    private static final int WITHDRAWAL_PERIOD_DAYS = 5;

    private final InvestmentContractRepository contractRepository;
    private final ContractStatusRepository contractStatusRepository;
    private final ContractStatusHistoryRepository statusHistoryRepository;
    private final InvestmentProposalRepository proposalRepository;
    private final InvestorRepository investorRepository;
    private final OperatorRepository operatorRepository;
    private final PersonalAccountRepository personalAccountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // ========================= Инвестор =========================

    @Transactional
    public InvestmentContractResponseDto create(CreateInvestmentContractDto dto, Long investorUserId) {
        Investor investor = investorRepository.findByUserId(investorUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Инвестор для пользователя с ID %d не найден".formatted(investorUserId)));

        InvestmentProposal proposal = proposalRepository.findById(dto.proposalId())
                .orElseThrow(() -> new InvestmentProposalNotFoundException(
                        "Инвестиционное предложение с ID %d не найдено".formatted(dto.proposalId())));

        if (!"active".equals(proposal.getStatus().getCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Инвестиционное предложение не в статусе 'Активно'");
        }

        if (LocalDate.now().isAfter(proposal.getProposalEndDate())) {
            throw new InvalidProposalStatusTransitionException(
                    "Срок действия инвестиционного предложения истёк");
        }

        if (contractRepository.existsByProposalIdAndInvestorId(dto.proposalId(), investor.getId())) {
            throw new IllegalArgumentException(
                    "У вас уже есть договор по данному инвестиционному предложению");
        }

        if (dto.amount().compareTo(proposal.getMinInvestmentAmount()) < 0) {
            throw new IllegalArgumentException(
                    "Сумма инвестирования не может быть меньше %s".formatted(proposal.getMinInvestmentAmount()));
        }

        BigDecimal remainingCapacity = proposal.getMaxInvestmentAmount().subtract(proposal.getCollectedAmount());
        if (dto.amount().compareTo(remainingCapacity) > 0) {
            throw new IllegalArgumentException(
                    "Сумма превышает оставшийся лимит сбора (%s)".formatted(remainingCapacity));
        }

        PersonalAccount account = investor.getPersonalAccount();
        BigDecimal availableBalance = account.getBalance().subtract(account.getHoldAmount());
        if (availableBalance.compareTo(dto.amount()) < 0) {
            throw new InsufficientFundsException(
                    "Недостаточно средств на счёте. Доступно: %s, требуется: %s"
                            .formatted(availableBalance, dto.amount()));
        }

        account.setBalance(account.getBalance().subtract(dto.amount()));
        personalAccountRepository.save(account);

        AccountTransaction transaction = AccountTransaction.builder()
                .personalAccount(account)
                .transactionType(TransactionType.TRANSFER_OUT)
                .amount(dto.amount())
                .balanceAfter(account.getBalance())
                .description("Инвестирование по ИП #%d: %s".formatted(proposal.getId(), proposal.getTitle()))
                .build();
        transactionRepository.save(transaction);

        proposal.setCollectedAmount(proposal.getCollectedAmount().add(dto.amount()));
        proposalRepository.save(proposal);

        ContractStatus reviewingStatus = contractStatusRepository.findByCode(REVIEWING_STATUS)
                .orElseThrow(() -> new IllegalStateException("Статус 'reviewing' не найден"));

        User investorUser = userRepository.findById(investorUserId).orElseThrow();

        InvestmentContract contract = InvestmentContract.builder()
                .contractNumber(generateContractNumber())
                .proposal(proposal)
                .investor(investor)
                .status(reviewingStatus)
                .amount(dto.amount())
                .signedAt(LocalDateTime.now())
                .build();
        contract = contractRepository.save(contract);

        saveStatusHistory(contract, null, reviewingStatus, investorUser, "Договор создан, средства списаны");

        return toResponseDto(contract);
    }

    @Transactional
    public InvestmentContractResponseDto withdraw(Long contractId, WithdrawContractDto dto, Long investorUserId) {
        InvestmentContract contract = findContractOrThrow(contractId);
        verifyInvestorOwnership(contract, investorUserId);

        String currentStatus = contract.getStatus().getCode();
        if (!REVIEWING_STATUS.equals(currentStatus) && !APPROVED_STATUS.equals(currentStatus)) {
            throw new ContractWithdrawalException(
                    "Отзыв возможен только для договоров в статусе 'На рассмотрении' или 'Одобрен'. Текущий: '%s'"
                            .formatted(contract.getStatus().getName()));
        }

        LocalDateTime withdrawalDeadline = contract.getCreatedAt().plusDays(WITHDRAWAL_PERIOD_DAYS);
        if (LocalDateTime.now().isAfter(withdrawalDeadline)) {
            throw new ContractWithdrawalException(
                    "Срок отзыва договора истёк (%d дней с момента создания)".formatted(WITHDRAWAL_PERIOD_DAYS));
        }

        InvestmentProposal proposal = contract.getProposal();
        if (LocalDate.now().isAfter(proposal.getProposalEndDate())) {
            throw new ContractWithdrawalException(
                    "Срок действия инвестиционного предложения истёк, отзыв невозможен");
        }

        ContractStatus oldStatus = contract.getStatus();
        ContractStatus withdrawnStatus = contractStatusRepository.findByCode(WITHDRAWN_STATUS)
                .orElseThrow(() -> new IllegalStateException("Статус 'withdrawn' не найден"));

        refundToInvestor(contract, "Возврат по отзыву договора #%s".formatted(contract.getContractNumber()));

        proposal.setCollectedAmount(proposal.getCollectedAmount().subtract(contract.getAmount()));
        proposalRepository.save(proposal);

        contract.setStatus(withdrawnStatus);
        contract.setWithdrawalReason(dto.reason());
        contract.setWithdrawnAt(LocalDateTime.now());
        contract.setLockedBy(null);
        contract.setLockHeartbeatAt(null);
        contract = contractRepository.save(contract);

        User investorUser = userRepository.findById(investorUserId).orElseThrow();
        saveStatusHistory(contract, oldStatus, withdrawnStatus, investorUser, dto.reason());

        return toResponseDto(contract);
    }

    @Transactional(readOnly = true)
    public InvestmentContractResponseDto getById(Long contractId) {
        return toResponseDto(findContractOrThrow(contractId));
    }

    @Transactional(readOnly = true)
    public Page<InvestmentContractListItemDto> getMyContracts(Long investorUserId, Pageable pageable) {
        return contractRepository.findByInvestorUserId(investorUserId, pageable)
                .map(this::toListItemDto);
    }

    // ========================= Оператор (модерация с блокировкой) =========================

    /**
     * Пул ДИ, доступных для захвата: reviewing + не заблокированные.
     */
    @Transactional(readOnly = true)
    public Page<InvestmentContractListItemDto> getForModeration(Pageable pageable) {
        return contractRepository.findAvailableForModeration(pageable)
                .map(this::toListItemDto);
    }

    /**
     * ДИ, закреплённые за текущим оператором.
     */
    @Transactional(readOnly = true)
    public Page<InvestmentContractListItemDto> getMyReviewing(Long operatorUserId, Pageable pageable) {
        return contractRepository.findLockedByOperator(operatorUserId, pageable)
                .map(this::toListItemDto);
    }

    /**
     * Атомарный захват ДИ оператором.
     * Если два оператора одновременно вызовут claim — только один получит updatedRows=1.
     */
    @Transactional
    public InvestmentContractResponseDto claimForReview(Long contractId, Long operatorUserId) {
        Operator operator = operatorRepository.findByUserId(operatorUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Оператор для пользователя с ID %d не найден".formatted(operatorUserId)));

        int updatedRows = contractRepository.claimForReview(contractId, operator.getId());

        if (updatedRows == 0) {
            throw new ProposalAlreadyClaimedException(
                    "Договор с ID %d уже забран другим оператором или недоступен для модерации"
                            .formatted(contractId));
        }

        InvestmentContract contract = findContractOrThrow(contractId);

        User changedBy = userRepository.findById(operatorUserId).orElseThrow();
        saveStatusHistory(contract, contract.getStatus(), contract.getStatus(), changedBy,
                "Оператор забрал договор на рассмотрение");

        return toResponseDto(contract);
    }

    /**
     * Heartbeat — оператор подтверждает, что всё ещё работает над ДИ.
     */
    @Transactional
    public void heartbeat(Long contractId, Long operatorUserId) {
        Operator operator = operatorRepository.findByUserId(operatorUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Оператор для пользователя с ID %d не найден".formatted(operatorUserId)));

        int updatedRows = contractRepository.refreshHeartbeat(contractId, operator.getId());

        if (updatedRows == 0) {
            throw new InvalidProposalStatusTransitionException(
                    "Договор с ID %d не закреплён за вами или уже завершён".formatted(contractId));
        }
    }

    /**
     * Оператор принимает решение: approved или rejected.
     * Только оператор, за которым закреплён ДИ, может вынести решение.
     * При rejected — средства возвращаются инвестору.
     */
    @Transactional
    public InvestmentContractResponseDto resolveReview(Long contractId, ChangeProposalStatusDto dto,
                                                        Long operatorUserId) {
        InvestmentContract contract = findContractOrThrow(contractId);

        if (!REVIEWING_STATUS.equals(contract.getStatus().getCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Договор не в статусе 'На рассмотрении'");
        }

        Operator operator = operatorRepository.findByUserId(operatorUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Оператор для пользователя с ID %d не найден".formatted(operatorUserId)));

        if (contract.getLockedBy() == null || !contract.getLockedBy().getId().equals(operator.getId())) {
            throw new InvalidProposalStatusTransitionException(
                    "Договор с ID %d не закреплён за вами".formatted(contractId));
        }

        if (!Set.of(APPROVED_STATUS, REJECTED_STATUS).contains(dto.statusCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Оператор может перевести договор только в 'approved' или 'rejected'. Получено: '%s'"
                            .formatted(dto.statusCode()));
        }

        ContractStatus oldStatus = contract.getStatus();
        ContractStatus newStatus = contractStatusRepository.findByCode(dto.statusCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Статус '%s' не найден".formatted(dto.statusCode())));

        contract.setStatus(newStatus);
        contract.setReviewedBy(operator);
        contract.setReviewedAt(LocalDateTime.now());
        contract.setLockedBy(null);
        contract.setLockHeartbeatAt(null);

        if (REJECTED_STATUS.equals(dto.statusCode())) {
            contract.setRejectionReason(dto.comment());

            refundToInvestor(contract,
                    "Возврат по отклонению договора #%s".formatted(contract.getContractNumber()));

            InvestmentProposal proposal = contract.getProposal();
            proposal.setCollectedAmount(proposal.getCollectedAmount().subtract(contract.getAmount()));
            proposalRepository.save(proposal);
        }

        contract = contractRepository.save(contract);

        User changedBy = userRepository.findById(operatorUserId).orElseThrow();
        saveStatusHistory(contract, oldStatus, newStatus, changedBy, dto.comment());

        return toResponseDto(contract);
    }

    /**
     * Вызывается планировщиком — освобождает ДИ с просроченными heartbeat (>5 мин).
     * Договор остаётся в reviewing, но снимается locked_by → возвращается в пул.
     */
    @Transactional
    public int releaseExpiredLocks() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(5);
        return contractRepository.releaseExpiredLocks(expiredBefore);
    }

    // ========================= Private =========================

    private void refundToInvestor(InvestmentContract contract, String description) {
        PersonalAccount account = contract.getInvestor().getPersonalAccount();
        account.setBalance(account.getBalance().add(contract.getAmount()));
        personalAccountRepository.save(account);

        AccountTransaction refundTx = AccountTransaction.builder()
                .personalAccount(account)
                .transactionType(TransactionType.TRANSFER_IN)
                .amount(contract.getAmount())
                .balanceAfter(account.getBalance())
                .description(description)
                .build();
        transactionRepository.save(refundTx);
    }

    private void verifyInvestorOwnership(InvestmentContract contract, Long investorUserId) {
        if (!contract.getInvestor().getUser().getId().equals(investorUserId)) {
            throw new InvalidProposalStatusTransitionException(
                    "Нет доступа к договору с ID %d".formatted(contract.getId()));
        }
    }

    private InvestmentContract findContractOrThrow(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new InvestmentContractNotFoundException(
                        "Договор инвестирования с ID %d не найден".formatted(id)));
    }

    private String generateContractNumber() {
        return "DI-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private void saveStatusHistory(InvestmentContract contract, ContractStatus oldStatus,
                                   ContractStatus newStatus, User changedBy, String comment) {
        ContractStatusHistory history = ContractStatusHistory.builder()
                .contract(contract)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .comment(comment)
                .build();
        statusHistoryRepository.save(history);
    }

    private InvestmentContractResponseDto toResponseDto(InvestmentContract c) {
        LocalDateTime withdrawalDeadline = c.getCreatedAt().plusDays(WITHDRAWAL_PERIOD_DAYS);
        LocalDateTime proposalEndDateTime = c.getProposal().getProposalEndDate().atStartOfDay();

        LocalDateTime effectiveDeadline = withdrawalDeadline.isBefore(proposalEndDateTime)
                ? withdrawalDeadline : proposalEndDateTime;

        return new InvestmentContractResponseDto(
                c.getId(),
                c.getContractNumber(),
                c.getProposal().getId(),
                c.getProposal().getTitle(),
                c.getInvestor().getId(),
                c.getStatus().getCode(),
                c.getStatus().getName(),
                c.getAmount(),
                c.getRejectionReason(),
                c.getWithdrawalReason(),
                c.getSignedAt(),
                c.getReviewedAt(),
                c.getWithdrawnAt(),
                c.getCompletedAt(),
                c.getFailedAt(),
                c.getCreatedAt(),
                effectiveDeadline
        );
    }

    private InvestmentContractListItemDto toListItemDto(InvestmentContract c) {
        return new InvestmentContractListItemDto(
                c.getId(),
                c.getContractNumber(),
                c.getProposal().getId(),
                c.getProposal().getTitle(),
                c.getStatus().getCode(),
                c.getStatus().getName(),
                c.getAmount(),
                c.getCreatedAt()
        );
    }
}
