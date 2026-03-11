package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.contract.InvestmentContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvestmentContractRepository extends JpaRepository<InvestmentContract, Long> {

    @EntityGraph(attributePaths = {"status", "proposal", "investor"})
    Optional<InvestmentContract> findById(Long id);

    boolean existsByProposalIdAndInvestorId(Long proposalId, Long investorId);

    @Query("""
            SELECT c FROM InvestmentContract c
            JOIN FETCH c.status
            JOIN FETCH c.proposal
            WHERE c.investor.user.id = :userId
            ORDER BY c.createdAt DESC
            """)
    Page<InvestmentContract> findByInvestorUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Пул модерации: reviewing + не заблокированные оператором.
     */
    @Query("""
            SELECT c FROM InvestmentContract c
            JOIN FETCH c.status
            JOIN FETCH c.proposal
            JOIN FETCH c.investor
            WHERE c.status.code = 'reviewing' AND c.lockedBy IS NULL
            ORDER BY c.createdAt ASC
            """)
    Page<InvestmentContract> findAvailableForModeration(Pageable pageable);

    /**
     * ДИ, закреплённые за конкретным оператором.
     */
    @Query("""
            SELECT c FROM InvestmentContract c
            JOIN FETCH c.status
            JOIN FETCH c.proposal
            JOIN FETCH c.investor
            WHERE c.status.code = 'reviewing' AND c.lockedBy.user.id = :operatorUserId
            ORDER BY c.createdAt ASC
            """)
    Page<InvestmentContract> findLockedByOperator(@Param("operatorUserId") Long operatorUserId, Pageable pageable);

    @Query("""
            SELECT c FROM InvestmentContract c
            JOIN FETCH c.status
            JOIN FETCH c.proposal
            JOIN FETCH c.investor
            WHERE c.proposal.id = :proposalId
            AND c.status.code IN :statusCodes
            """)
    List<InvestmentContract> findByProposalIdAndStatusCodes(@Param("proposalId") Long proposalId,
                                                            @Param("statusCodes") List<String> statusCodes);

    /**
     * Атомарный захват ДИ оператором.
     * WHERE locked_by IS NULL гарантирует, что только один оператор получит строку.
     */
    @Modifying
    @Query(value = """
            UPDATE investment_contracts
            SET locked_by = :operatorId,
                lock_heartbeat_at = now(),
                version = version + 1
            WHERE id = :contractId
              AND locked_by IS NULL
              AND status_id = (SELECT id FROM contract_statuses WHERE code = 'reviewing')
            """, nativeQuery = true)
    int claimForReview(@Param("contractId") Long contractId, @Param("operatorId") Long operatorId);

    /**
     * Heartbeat — обновляет метку времени, только если ДИ закреплено за этим оператором.
     */
    @Modifying
    @Query(value = """
            UPDATE investment_contracts
            SET lock_heartbeat_at = now()
            WHERE id = :contractId
              AND locked_by = :operatorId
              AND status_id = (SELECT id FROM contract_statuses WHERE code = 'reviewing')
            """, nativeQuery = true)
    int refreshHeartbeat(@Param("contractId") Long contractId, @Param("operatorId") Long operatorId);

    /**
     * Освобождение просроченных блокировок — ДИ остаётся в reviewing, но снимается locked_by.
     */
    @Modifying
    @Query(value = """
            UPDATE investment_contracts
            SET locked_by = NULL,
                lock_heartbeat_at = NULL,
                version = version + 1
            WHERE locked_by IS NOT NULL
              AND lock_heartbeat_at < :expiredBefore
              AND status_id = (SELECT id FROM contract_statuses WHERE code = 'reviewing')
            """, nativeQuery = true)
    int releaseExpiredLocks(@Param("expiredBefore") LocalDateTime expiredBefore);
}
