package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.proposal.InvestmentProposal;
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
}
