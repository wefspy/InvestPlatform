package com.example.investplatform.application.dto.account;

import com.example.investplatform.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountTransactionDto(
        Long id,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String description,
        LocalDateTime createdAt
) {
}
