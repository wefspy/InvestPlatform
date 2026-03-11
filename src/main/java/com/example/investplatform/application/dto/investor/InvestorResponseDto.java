package com.example.investplatform.application.dto.investor;

import com.example.investplatform.model.enums.InvestorType;

public record InvestorResponseDto(
        Long id,
        String email,
        InvestorType investorType,
        String accountNumber
) {
}
