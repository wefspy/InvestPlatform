package com.example.investplatform.application.service;

import com.example.investplatform.infrastructure.repository.PaymentRepository;
import com.example.investplatform.model.entity.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PaymentReconciliationService {

    private static final Set<String> TERMINAL_STATUSES = Set.of("succeeded", "canceled");
    private static final Duration STALE_THRESHOLD_ON_READ = Duration.ofSeconds(30);
    private static final Duration STALE_THRESHOLD_SCHEDULED = Duration.ofMinutes(5);

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public PaymentReconciliationService(
            PaymentRepository paymentRepository,
            @Lazy PaymentService paymentService) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    public Payment refreshIfStale(Payment payment) {
        if (TERMINAL_STATUSES.contains(payment.getYukassaStatus())) {
            return payment;
        }
        LocalDateTime updatedAt = payment.getUpdatedAt();
        if (updatedAt != null && updatedAt.isAfter(LocalDateTime.now().minus(STALE_THRESHOLD_ON_READ))) {
            return payment;
        }
        try {
            paymentService.applyPaymentStateFromApi(payment.getYukassaPaymentId());
            return paymentRepository.findById(payment.getId()).orElse(payment);
        } catch (Exception e) {
            log.warn("Не удалось перезапросить статус платежа {} у ЮKassa: {}",
                    payment.getYukassaPaymentId(), e.getMessage());
            return payment;
        }
    }

    @Scheduled(fixedRate = 60_000)
    public void reconcilePendingPayments() {
        LocalDateTime cutoff = LocalDateTime.now().minus(STALE_THRESHOLD_SCHEDULED);
        List<Payment> stale = paymentRepository
                .findTop50ByYukassaStatusNotInAndUpdatedAtBeforeOrderByUpdatedAtAsc(
                        TERMINAL_STATUSES, cutoff);
        if (stale.isEmpty()) {
            return;
        }
        log.info("Сверка с ЮKassa: {} нефинальных платежей старше {} мин",
                stale.size(), STALE_THRESHOLD_SCHEDULED.toMinutes());
        int updated = 0;
        for (Payment payment : stale) {
            try {
                paymentService.applyPaymentStateFromApi(payment.getYukassaPaymentId());
                updated++;
            } catch (Exception e) {
                log.warn("Сбой сверки платежа {}: {}",
                        payment.getYukassaPaymentId(), e.getMessage());
            }
        }
        if (updated > 0) {
            log.info("Сверка с ЮKassa: обновлено {} из {} платежей", updated, stale.size());
        }
    }
}
