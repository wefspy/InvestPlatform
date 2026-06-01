package com.example.investplatform.application.dto.investor;

public record InvestorLegalEntityDataDto(
        String fullName,
        String shortName,
        String ogrn,
        String inn,
        String foreignRegistrationInfo,
        String tin,
        String legalAddress,
        String postalAddress,
        String contactEmail,
        String phone
) {
}
