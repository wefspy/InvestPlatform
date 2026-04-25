package com.example.investplatform.application.dto.payment;

import java.time.LocalDateTime;

public record PaymentStatusDto(
        Long id,
        String yukassaPaymentId,
        String yukassaStatus,
        LocalDateTime paidAt,
        LocalDateTime canceledAt,
        String cancellationReason,
        LocalDateTime updatedAt
) {
}
