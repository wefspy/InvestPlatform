package com.example.investplatform.application.dto.payment;

import com.example.investplatform.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentHistoryItemDto(
        Long id,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String description,
        LocalDateTime createdAt,
        Long paymentId,
        String yukassaPaymentId,
        String yukassaStatus,
        String paymentMethodType,
        String receiptUrl,
        LocalDateTime paidAt
) {
}
