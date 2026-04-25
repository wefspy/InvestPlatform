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
import java.math.RoundingMode;
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
    private static final String COMPLETED_STATUS = "completed";
    private static final String FAILED_STATUS = "failed";

    private static final int WITHDRAWAL_PERIOD_DAYS = 5;
    private static final String PLATFORM_ACCOUNT_NUMBER = "PLATFORM-001";

    private final InvestmentContractRepository contractRepository;
    private final ContractStatusRepository contractStatusRepository;
    private final ContractStatusHistoryRepository statusHistoryRepository;
    private final InvestmentProposalRepository proposalRepository;
    private final InvestorRepository investorRepository;
    private final OperatorRepository operatorRepository;
    private final PersonalAccountRepository personalAccountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CommissionService commissionService;

    // ========================= Инвестор =========================

    @Transactional(readOnly = true)
    public ContractCalculationDto calculate(Long proposalId, Long securitiesQuantity) {
        InvestmentProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new InvestmentProposalNotFoundException(
                        "Инвестиционное предложение с ID %d не найдено".formatted(proposalId)));

        BigDecimal pricePerUnit = proposal.getPricePerUnit();
        BigDecimal investmentAmount = pricePerUnit
                .multiply(BigDecimal.valueOf(securitiesQuantity))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal commissionAmount = commissionService.calculate(investmentAmount);

        return new ContractCalculationDto(
                securitiesQuantity,
                pricePerUnit,
                investmentAmount,
                commissionAmount,
                investmentAmount.add(commissionAmount)
        );
    }

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

        long quantity = dto.securitiesQuantity();

        if (proposal.getMinPurchaseQuantity() != null && quantity < proposal.getMinPurchaseQuantity()) {
            throw new IllegalArgumentException(
                    "Количество ценных бумаг не может быть меньше %d".formatted(proposal.getMinPurchaseQuantity()));
        }

        if (proposal.getMaxPurchaseQuantity() != null && quantity > proposal.getMaxPurchaseQuantity()) {
            throw new IllegalArgumentException(
                    "Количество ценных бумаг не может быть больше %d".formatted(proposal.getMaxPurchaseQuantity()));
        }

        BigDecimal pricePerUnit = proposal.getPricePerUnit();
        BigDecimal amount = pricePerUnit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal commissionAmount = commissionService.calculate(amount);
        BigDecimal totalCharge = amount.add(commissionAmount);

        PersonalAccount account = investor.getPersonalAccount();
        BigDecimal availableBalance = account.getBalance().subtract(account.getHoldAmount());
        if (availableBalance.compareTo(totalCharge) < 0) {
            throw new InsufficientFundsException(
                    "Недостаточно средств на счёте. Доступно: %s, требуется: %s (инвестиция: %s, комиссия: %s)"
                            .formatted(availableBalance, totalCharge, amount, commissionAmount));
        }

        // Атомарное резервирование под кэп: collected + reserved + amount <= max.
        // При гонке двух инвесторов один получит updatedRows=1, второй — 0.
        int reserved = proposalRepository.reserveAmount(proposal.getId(), amount);
        if (reserved == 0) {
            BigDecimal remainingCapacity = proposal.getMaxInvestmentAmount()
                    .subtract(proposal.getCollectedAmount())
                    .subtract(proposal.getReservedAmount());
            throw new IllegalArgumentException(
                    "Сумма превышает оставшийся лимит сбора (%s) или ИП больше недоступно для инвестиций"
                            .formatted(remainingCapacity.max(BigDecimal.ZERO)));
        }

        account.setHoldAmount(account.getHoldAmount().add(totalCharge));
        personalAccountRepository.save(account);

        AccountTransaction holdTx = AccountTransaction.builder()
                .personalAccount(account)
                .transactionType(TransactionType.HOLD)
                .amount(totalCharge)
                .balanceAfter(account.getBalance())
                .description("Блокировка средств по ИП #%d: %s (инвестиция: %s, комиссия: %s)"
                        .formatted(proposal.getId(), proposal.getTitle(), amount, commissionAmount))
                .build();
        transactionRepository.save(holdTx);

        ContractStatus reviewingStatus = contractStatusRepository.findByCode(REVIEWING_STATUS)
                .orElseThrow(() -> new IllegalStateException("Статус 'reviewing' не найден"));

        User investorUser = userRepository.findById(investorUserId).orElseThrow();

        InvestmentContract contract = InvestmentContract.builder()
                .contractNumber(generateContractNumber())
                .proposal(proposal)
                .investor(investor)
                .status(reviewingStatus)
                .amount(amount)
                .commissionAmount(commissionAmount)
                .securitiesQuantity(BigDecimal.valueOf(quantity))
                .pricePerSecurity(pricePerUnit)
                .security(proposal.getSecurity())
                .signedAt(LocalDateTime.now())
                .build();
        contract = contractRepository.save(contract);

        saveStatusHistory(contract, null, reviewingStatus, investorUser, "Договор создан, средства заблокированы");

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

        releaseHoldToInvestor(contract, "Разблокировка по отзыву договора #%s".formatted(contract.getContractNumber()));

        // reviewing → reserved уменьшается; approved → collected уменьшается.
        if (REVIEWING_STATUS.equals(oldStatus.getCode())) {
            proposalRepository.releaseReservedAmount(proposal.getId(), contract.getAmount());
        } else {
            proposalRepository.releaseCollectedAmount(proposal.getId(), contract.getAmount());
        }

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

        InvestmentProposal proposal = contract.getProposal();
        if (REJECTED_STATUS.equals(dto.statusCode())) {
            contract.setRejectionReason(dto.comment());

            releaseHoldToInvestor(contract,
                    "Разблокировка по отклонению договора #%s".formatted(contract.getContractNumber()));

            proposalRepository.releaseReservedAmount(proposal.getId(), contract.getAmount());
        } else {
            // approved: резерв подтверждается → переходит в collected.
            proposalRepository.confirmReservedAmount(proposal.getId(), contract.getAmount());
        }

        contract = contractRepository.save(contract);

        User changedBy = userRepository.findById(operatorUserId).orElseThrow();
        saveStatusHistory(contract, oldStatus, newStatus, changedBy, dto.comment());

        return toResponseDto(contract);
    }

    /**
     * Завершение договора оператором: approved → completed.
     * Разблокировка средств у инвестора, перевод суммы эмитенту, комиссии — платформе.
     */
    @Transactional
    public InvestmentContractResponseDto complete(Long contractId, Long operatorUserId) {
        InvestmentContract contract = findContractOrThrow(contractId);

        if (!APPROVED_STATUS.equals(contract.getStatus().getCode())) {
            throw new InvalidProposalStatusTransitionException(
                    "Завершение возможно только для договоров в статусе 'Одобрен'. Текущий: '%s'"
                            .formatted(contract.getStatus().getName()));
        }

        Operator operator = operatorRepository.findByUserId(operatorUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Оператор для пользователя с ID %d не найден".formatted(operatorUserId)));

        User changedBy = userRepository.findById(operatorUserId).orElseThrow();
        ContractStatus oldStatus = contract.getStatus();
        contract = doCompleteContract(contract);
        saveStatusHistory(contract, oldStatus, contract.getStatus(), changedBy,
                "Договор завершён оператором #%d, средства переведены эмитенту".formatted(operator.getId()));

        return toResponseDto(contract);
    }

    // ========================= Системные переходы (каскад при закрытии ИП) =========================

    /**
     * Системное завершение approved-договора при успешном закрытии ИП.
     * Та же логика, что и complete(), но без оператора.
     */
    @Transactional
    public void completeAsSystem(Long contractId, String comment) {
        InvestmentContract contract = findContractOrThrow(contractId);
        if (!APPROVED_STATUS.equals(contract.getStatus().getCode())) {
            return;
        }
        ContractStatus oldStatus = contract.getStatus();
        contract = doCompleteContract(contract);
        saveStatusHistory(contract, oldStatus, contract.getStatus(), null, comment);
    }

    /**
     * Системный перевод договора в failed: средства возвращаются инвестору,
     * collected/reserved у ИП уменьшается. Используется при неуспешном закрытии ИП.
     */
    @Transactional
    public void failAsSystem(Long contractId, String comment) {
        InvestmentContract contract = findContractOrThrow(contractId);
        String currentCode = contract.getStatus().getCode();
        if (!REVIEWING_STATUS.equals(currentCode) && !APPROVED_STATUS.equals(currentCode)) {
            return;
        }

        ContractStatus oldStatus = contract.getStatus();
        ContractStatus failedStatus = contractStatusRepository.findByCode(FAILED_STATUS)
                .orElseThrow(() -> new IllegalStateException("Статус 'failed' не найден"));

        releaseHoldToInvestor(contract,
                "Возврат средств по неуспешному закрытию ИП, договор #%s".formatted(contract.getContractNumber()));

        InvestmentProposal proposal = contract.getProposal();
        if (REVIEWING_STATUS.equals(currentCode)) {
            proposalRepository.releaseReservedAmount(proposal.getId(), contract.getAmount());
        } else {
            proposalRepository.releaseCollectedAmount(proposal.getId(), contract.getAmount());
        }

        contract.setStatus(failedStatus);
        contract.setFailedAt(LocalDateTime.now());
        contract.setLockedBy(null);
        contract.setLockHeartbeatAt(null);
        contract = contractRepository.save(contract);

        saveStatusHistory(contract, oldStatus, failedStatus, null, comment);
    }

    /**
     * Системное отклонение reviewing-договора при успешном закрытии ИП:
     * оператор не успел рассмотреть до окончания срока. Средства возвращаются инвестору.
     */
    @Transactional
    public void rejectAsSystem(Long contractId, String comment) {
        InvestmentContract contract = findContractOrThrow(contractId);
        if (!REVIEWING_STATUS.equals(contract.getStatus().getCode())) {
            return;
        }

        ContractStatus oldStatus = contract.getStatus();
        ContractStatus rejectedStatus = contractStatusRepository.findByCode(REJECTED_STATUS)
                .orElseThrow(() -> new IllegalStateException("Статус 'rejected' не найден"));

        releaseHoldToInvestor(contract,
                "Возврат средств: оператор не успел рассмотреть, договор #%s"
                        .formatted(contract.getContractNumber()));

        InvestmentProposal proposal = contract.getProposal();
        proposalRepository.releaseReservedAmount(proposal.getId(), contract.getAmount());

        contract.setStatus(rejectedStatus);
        contract.setRejectionReason(comment);
        contract.setReviewedAt(LocalDateTime.now());
        contract.setLockedBy(null);
        contract.setLockHeartbeatAt(null);
        contract = contractRepository.save(contract);

        saveStatusHistory(contract, oldStatus, rejectedStatus, null, comment);
    }

    /**
     * Внутренняя логика завершения approved → completed:
     * перевод холда инвестора эмитенту, комиссии — платформе.
     */
    private InvestmentContract doCompleteContract(InvestmentContract contract) {
        ContractStatus completedStatus = contractStatusRepository.findByCode(COMPLETED_STATUS)
                .orElseThrow(() -> new IllegalStateException("Статус 'completed' не найден"));

        BigDecimal amount = contract.getAmount();
        BigDecimal commission = contract.getCommissionAmount();
        BigDecimal totalCharge = amount.add(commission);

        PersonalAccount investorAccount = contract.getInvestor().getPersonalAccount();
        investorAccount.setHoldAmount(investorAccount.getHoldAmount().subtract(totalCharge));
        investorAccount.setBalance(investorAccount.getBalance().subtract(totalCharge));
        personalAccountRepository.save(investorAccount);

        AccountTransaction releaseTx = AccountTransaction.builder()
                .personalAccount(investorAccount)
                .transactionType(TransactionType.RELEASE)
                .amount(totalCharge)
                .balanceAfter(investorAccount.getBalance().add(totalCharge))
                .description("Разблокировка при завершении договора #%s".formatted(contract.getContractNumber()))
                .build();
        transactionRepository.save(releaseTx);

        AccountTransaction investTx = AccountTransaction.builder()
                .personalAccount(investorAccount)
                .transactionType(TransactionType.TRANSFER_OUT)
                .amount(amount)
                .balanceAfter(investorAccount.getBalance().add(commission))
                .description("Инвестирование по ИП #%d, договор #%s"
                        .formatted(contract.getProposal().getId(), contract.getContractNumber()))
                .build();
        transactionRepository.save(investTx);

        AccountTransaction commissionTx = AccountTransaction.builder()
                .personalAccount(investorAccount)
                .transactionType(TransactionType.COMMISSION)
                .amount(commission)
                .balanceAfter(investorAccount.getBalance())
                .description("Комиссия платформы по договору #%s".formatted(contract.getContractNumber()))
                .build();
        transactionRepository.save(commissionTx);

        PersonalAccount emitentAccount = contract.getProposal().getEmitent().getPersonalAccount();
        emitentAccount.setBalance(emitentAccount.getBalance().add(amount));
        personalAccountRepository.save(emitentAccount);

        AccountTransaction emitentTx = AccountTransaction.builder()
                .personalAccount(emitentAccount)
                .transactionType(TransactionType.TRANSFER_IN)
                .amount(amount)
                .balanceAfter(emitentAccount.getBalance())
                .description("Поступление по договору #%s, ИП #%d"
                        .formatted(contract.getContractNumber(), contract.getProposal().getId()))
                .build();
        transactionRepository.save(emitentTx);

        PersonalAccount platformAccount = personalAccountRepository
                .findByAccountNumber(PLATFORM_ACCOUNT_NUMBER)
                .orElseThrow(() -> new IllegalStateException("Счёт платформы не найден"));
        platformAccount.setBalance(platformAccount.getBalance().add(commission));
        personalAccountRepository.save(platformAccount);

        AccountTransaction platformTx = AccountTransaction.builder()
                .personalAccount(platformAccount)
                .transactionType(TransactionType.TRANSFER_IN)
                .amount(commission)
                .balanceAfter(platformAccount.getBalance())
                .description("Комиссия по договору #%s".formatted(contract.getContractNumber()))
                .build();
        transactionRepository.save(platformTx);

        contract.setStatus(completedStatus);
        contract.setCompletedAt(LocalDateTime.now());
        return contractRepository.save(contract);
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

    private void releaseHoldToInvestor(InvestmentContract contract, String description) {
        BigDecimal holdTotal = contract.getAmount().add(contract.getCommissionAmount());

        PersonalAccount account = contract.getInvestor().getPersonalAccount();
        account.setHoldAmount(account.getHoldAmount().subtract(holdTotal));
        personalAccountRepository.save(account);

        AccountTransaction releaseTx = AccountTransaction.builder()
                .personalAccount(account)
                .transactionType(TransactionType.RELEASE)
                .amount(holdTotal)
                .balanceAfter(account.getBalance())
                .description(description)
                .build();
        transactionRepository.save(releaseTx);
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
                c.getSecuritiesQuantity(),
                c.getPricePerSecurity(),
                c.getCommissionAmount(),
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
                c.getSecuritiesQuantity(),
                c.getCommissionAmount(),
                c.getCreatedAt()
        );
    }
}
