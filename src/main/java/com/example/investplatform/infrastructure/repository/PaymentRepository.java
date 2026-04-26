package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.payment.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByYukassaPaymentId(String yukassaPaymentId);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Page<Payment> findByPersonalAccountId(Long personalAccountId, Pageable pageable);

    List<Payment> findTop50ByYukassaStatusNotInAndUpdatedAtBeforeOrderByUpdatedAtAsc(
            Collection<String> excludedStatuses, LocalDateTime updatedBefore);

    @Query("SELECT p FROM Payment p WHERE p.personalAccount.id = :accountId " +
            "AND NOT EXISTS (SELECT 1 FROM AccountTransaction t WHERE t.payment = p) " +
            "ORDER BY p.createdAt DESC")
    List<Payment> findOrphanByPersonalAccountId(@Param("accountId") Long accountId);
}
