package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.proposal.InvestmentProposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvestmentProposalRepository extends JpaRepository<InvestmentProposal, Long> {

    @EntityGraph(attributePaths = {"status", "investmentMethod", "emitent"})
    Optional<InvestmentProposal> findById(Long id);

    @Query("""
            SELECT p FROM InvestmentProposal p
            JOIN FETCH p.status
            JOIN FETCH p.investmentMethod
            WHERE p.emitent.id = :emitentId
            ORDER BY p.createdAt DESC
            """)
    Page<InvestmentProposal> findByEmitentId(@Param("emitentId") Long emitentId, Pageable pageable);

    @Query("""
            SELECT p FROM InvestmentProposal p
            JOIN FETCH p.status
            JOIN FETCH p.investmentMethod
            WHERE p.emitent.user.id = :userId
            ORDER BY p.createdAt DESC
            """)
    Page<InvestmentProposal> findByEmitentUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT p FROM InvestmentProposal p
            JOIN FETCH p.status
            JOIN FETCH p.investmentMethod
            WHERE p.status.code = 'pending' AND p.lockedBy IS NULL
            ORDER BY p.submittedAt ASC
            """)
    Page<InvestmentProposal> findAvailableForModeration(Pageable pageable);

    @Query("""
            SELECT p FROM InvestmentProposal p
            JOIN FETCH p.status
            JOIN FETCH p.investmentMethod
            WHERE p.status.code = 'reviewing' AND p.lockedBy.user.id = :operatorUserId
            ORDER BY p.createdAt DESC
            """)
    Page<InvestmentProposal> findLockedByOperator(@Param("operatorUserId") Long operatorUserId, Pageable pageable);

    @Query("""
            SELECT p FROM InvestmentProposal p
            JOIN FETCH p.status
            JOIN FETCH p.investmentMethod
            WHERE p.status.code IN :statusCodes
            ORDER BY p.createdAt DESC
            """)
    Page<InvestmentProposal> findByStatusCodes(@Param("statusCodes") List<String> statusCodes, Pageable pageable);

    @Query("""
            SELECT p FROM InvestmentProposal p
            JOIN FETCH p.status
            JOIN FETCH p.investmentMethod
            WHERE p.status.code = :statusCode
            ORDER BY p.createdAt DESC
            """)
    Page<InvestmentProposal> findByStatusCode(@Param("statusCode") String statusCode, Pageable pageable);

    @Query("""
            SELECT p FROM InvestmentProposal p
            JOIN FETCH p.status
            JOIN FETCH p.investmentMethod
            ORDER BY p.createdAt DESC
            """)
    Page<InvestmentProposal> findAllWithDetails(Pageable pageable);

    /**
     * Активные ИП с фильтрами для каталога.
     * Все параметры опциональны; null означает «без фильтра по этому полю».
     * Параметр onlyAvailable=true оставляет только ИП, где (collected + reserved) меньше максимума.
     */
    @Query(value = """
            SELECT p FROM InvestmentProposal p
            JOIN FETCH p.status s
            JOIN FETCH p.investmentMethod m
            WHERE s.code = 'active'
              AND (:q IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:methodCode IS NULL OR m.code = :methodCode)
              AND (:emitentId IS NULL OR p.emitent.id = :emitentId)
              AND (:minAmountFrom IS NULL OR p.minInvestmentAmount >= :minAmountFrom)
              AND (:minAmountTo IS NULL OR p.minInvestmentAmount <= :minAmountTo)
              AND (:maxAmountFrom IS NULL OR p.maxInvestmentAmount >= :maxAmountFrom)
              AND (:maxAmountTo IS NULL OR p.maxInvestmentAmount <= :maxAmountTo)
              AND (:priceFrom IS NULL OR p.pricePerUnit >= :priceFrom)
              AND (:priceTo IS NULL OR p.pricePerUnit <= :priceTo)
              AND (:endDateFrom IS NULL OR p.proposalEndDate >= :endDateFrom)
              AND (:endDateTo IS NULL OR p.proposalEndDate <= :endDateTo)
              AND (:hasPreemptiveRight IS NULL OR p.hasPreemptiveRight = :hasPreemptiveRight)
              AND (:onlyAvailable = FALSE OR p.collectedAmount + p.reservedAmount < p.maxInvestmentAmount)
            """,
            countQuery = """
            SELECT COUNT(p) FROM InvestmentProposal p
            WHERE p.status.code = 'active'
              AND (:q IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:methodCode IS NULL OR p.investmentMethod.code = :methodCode)
              AND (:emitentId IS NULL OR p.emitent.id = :emitentId)
              AND (:minAmountFrom IS NULL OR p.minInvestmentAmount >= :minAmountFrom)
              AND (:minAmountTo IS NULL OR p.minInvestmentAmount <= :minAmountTo)
              AND (:maxAmountFrom IS NULL OR p.maxInvestmentAmount >= :maxAmountFrom)
              AND (:maxAmountTo IS NULL OR p.maxInvestmentAmount <= :maxAmountTo)
              AND (:priceFrom IS NULL OR p.pricePerUnit >= :priceFrom)
              AND (:priceTo IS NULL OR p.pricePerUnit <= :priceTo)
              AND (:endDateFrom IS NULL OR p.proposalEndDate >= :endDateFrom)
              AND (:endDateTo IS NULL OR p.proposalEndDate <= :endDateTo)
              AND (:hasPreemptiveRight IS NULL OR p.hasPreemptiveRight = :hasPreemptiveRight)
              AND (:onlyAvailable = FALSE OR p.collectedAmount + p.reservedAmount < p.maxInvestmentAmount)
            """)
    Page<InvestmentProposal> findActiveWithFilters(
            @Param("q") String q,
            @Param("methodCode") String investmentMethodCode,
            @Param("emitentId") Long emitentId,
            @Param("minAmountFrom") BigDecimal minInvestmentAmountFrom,
            @Param("minAmountTo") BigDecimal minInvestmentAmountTo,
            @Param("maxAmountFrom") BigDecimal maxInvestmentAmountFrom,
            @Param("maxAmountTo") BigDecimal maxInvestmentAmountTo,
            @Param("priceFrom") BigDecimal pricePerUnitFrom,
            @Param("priceTo") BigDecimal pricePerUnitTo,
            @Param("endDateFrom") LocalDate endDateFrom,
            @Param("endDateTo") LocalDate endDateTo,
            @Param("hasPreemptiveRight") Boolean hasPreemptiveRight,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);

    /**
     * Атомарный захват ИП оператором.
     * WHERE locked_by IS NULL гарантирует, что только один оператор получит строку.
     * Возвращает количество обновлённых строк (0 = кто-то уже забрал).
     */
    @Modifying
    @Query(value = """
            UPDATE investment_proposals
            SET locked_by = :operatorId,
                lock_heartbeat_at = now(),
                status_id = (SELECT id FROM proposal_statuses WHERE code = 'reviewing'),
                version = version + 1
            WHERE id = :proposalId
              AND locked_by IS NULL
              AND status_id = (SELECT id FROM proposal_statuses WHERE code = 'pending')
            """, nativeQuery = true)
    int claimForReview(@Param("proposalId") Long proposalId, @Param("operatorId") Long operatorId);

    /**
     * Heartbeat — обновляет метку времени, только если ИП всё ещё закреплено за этим оператором.
     */
    @Modifying
    @Query(value = """
            UPDATE investment_proposals
            SET lock_heartbeat_at = now()
            WHERE id = :proposalId
              AND locked_by = :operatorId
              AND status_id = (SELECT id FROM proposal_statuses WHERE code = 'reviewing')
            """, nativeQuery = true)
    int refreshHeartbeat(@Param("proposalId") Long proposalId, @Param("operatorId") Long operatorId);

    /**
     * Освобождение просроченных блокировок — возвращает ИП в пул pending.
     */
    @Modifying
    @Query(value = """
            UPDATE investment_proposals
            SET locked_by = NULL,
                lock_heartbeat_at = NULL,
                status_id = (SELECT id FROM proposal_statuses WHERE code = 'pending'),
                version = version + 1
            WHERE locked_by IS NOT NULL
              AND lock_heartbeat_at < :expiredBefore
              AND status_id = (SELECT id FROM proposal_statuses WHERE code = 'reviewing')
            """, nativeQuery = true)
    int releaseExpiredLocks(@Param("expiredBefore") LocalDateTime expiredBefore);

    /**
     * Атомарное резервирование суммы под новый ДИ.
     * WHERE-условие гарантирует:
     *   - ИП в статусе active;
     *   - срок ИП не истёк;
     *   - суммарный кэп (collected + reserved + amount) не превышает max.
     * При гонке двух инвесторов один получит updatedRows=1, второй — 0.
     */
    @Modifying
    @Query(value = """
            UPDATE investment_proposals
            SET reserved_amount = reserved_amount + :amount,
                version = version + 1
            WHERE id = :proposalId
              AND status_id = (SELECT id FROM proposal_statuses WHERE code = 'active')
              AND proposal_end_date >= CURRENT_DATE
              AND collected_amount + reserved_amount + :amount <= max_investment_amount
            """, nativeQuery = true)
    int reserveAmount(@Param("proposalId") Long proposalId, @Param("amount") BigDecimal amount);

    /**
     * Освобождение зарезервированной суммы (отзыв/отклонение reviewing-договора, либо отмена при закрытии ИП).
     */
    @Modifying
    @Query(value = """
            UPDATE investment_proposals
            SET reserved_amount = reserved_amount - :amount,
                version = version + 1
            WHERE id = :proposalId
            """, nativeQuery = true)
    int releaseReservedAmount(@Param("proposalId") Long proposalId, @Param("amount") BigDecimal amount);

    /**
     * Подтверждение резерва (reviewing → approved): резерв уменьшается, collected растёт.
     */
    @Modifying
    @Query(value = """
            UPDATE investment_proposals
            SET reserved_amount = reserved_amount - :amount,
                collected_amount = collected_amount + :amount,
                version = version + 1
            WHERE id = :proposalId
            """, nativeQuery = true)
    int confirmReservedAmount(@Param("proposalId") Long proposalId, @Param("amount") BigDecimal amount);

    /**
     * Уменьшение collected (отзыв approved-договора, либо неуспешное закрытие ИП).
     */
    @Modifying
    @Query(value = """
            UPDATE investment_proposals
            SET collected_amount = collected_amount - :amount,
                version = version + 1
            WHERE id = :proposalId
            """, nativeQuery = true)
    int releaseCollectedAmount(@Param("proposalId") Long proposalId, @Param("amount") BigDecimal amount);

    /**
     * ID активных ИП с истёкшим сроком — для пакетного автозакрытия планировщиком.
     */
    @Query("""
            SELECT p.id FROM InvestmentProposal p
            WHERE p.status.code = 'active'
              AND p.proposalEndDate < :today
            """)
    List<Long> findExpiredActiveProposalIds(@Param("today") LocalDate today);

    /**
     * Перевод ИП в финальный статус (completed/failed) с проставлением closed_at.
     * Делается нативным UPDATE'ом, чтобы не конфликтовать с @Version после каскадных
     * нативных операций над тем же ИП внутри той же транзакции.
     */
    @Modifying
    @Query(value = """
            UPDATE investment_proposals
            SET status_id = (SELECT id FROM proposal_statuses WHERE code = :statusCode),
                closed_at = :closedAt,
                locked_by = NULL,
                lock_heartbeat_at = NULL,
                version = version + 1
            WHERE id = :proposalId
              AND status_id = (SELECT id FROM proposal_statuses WHERE code = 'active')
            """, nativeQuery = true)
    int closeProposal(@Param("proposalId") Long proposalId,
                      @Param("statusCode") String statusCode,
                      @Param("closedAt") LocalDateTime closedAt);
}
