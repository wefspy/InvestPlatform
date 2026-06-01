package com.example.investplatform.application.dto.emitent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmitentPrivateEntrepreneurDataDto(
        String lastName,
        String firstName,
        String patronymic,
        LocalDate birthDate,
        String birthPlace,
        String ogrnip,
        String inn,
        String registrationAddress,
        String snils,
        String materialFacts,
        BigDecimal investedCurrentYear
) {
}
