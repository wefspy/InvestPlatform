package com.example.investplatform.application.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDto(
        Long id,
        String yukassaPaymentId,
        String yukassaStatus,
        BigDecimal amount,
        String currency,
        String paymentMethodType,
        String description,
        String confirmationToken,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
}
