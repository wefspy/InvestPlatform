package com.example.investplatform.application.dto.account;

import java.math.BigDecimal;

public record AccountBalanceDto(
        String accountNumber,
        BigDecimal balance,
        BigDecimal holdAmount,
        BigDecimal availableBalance
) {
}
