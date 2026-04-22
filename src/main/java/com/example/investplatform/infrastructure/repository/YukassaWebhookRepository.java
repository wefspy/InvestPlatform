package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.payment.YukassaWebhook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YukassaWebhookRepository extends JpaRepository<YukassaWebhook, Long> {
    boolean existsByEventId(String eventId);
}
