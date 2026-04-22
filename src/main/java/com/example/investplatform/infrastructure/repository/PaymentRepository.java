package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByYukassaPaymentId(String yukassaPaymentId);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
