package com.example.investplatform.application.dto.investor;

import com.example.investplatform.model.enums.InvestorType;

public record InvestorDetailDto(
        Long id,
        String email,
        InvestorType investorType,
        String accountNumber,
        Boolean isQualified,
        Boolean riskDeclarationAccepted,
        InvestorIndividualDataDto individual,
        InvestorPrivateEntrepreneurDataDto privateEntrepreneur,
        InvestorLegalEntityDataDto legalEntity
) {
}
