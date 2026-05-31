package com.example.investplatform.application.dto.emitent;

import java.math.BigDecimal;

public record EmitentLegalEntityDataDto(
        String fullName,
        String shortName,
        String ogrn,
        String inn,
        String kpp,
        String legalAddress,
        String postalAddress,
        String okpo,
        String okato,
        String organisationForm,
        String materialFacts,
        BigDecimal investedCurrentYear
) {
}
