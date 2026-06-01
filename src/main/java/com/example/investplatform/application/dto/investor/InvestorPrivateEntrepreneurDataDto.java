package com.example.investplatform.application.dto.investor;

import com.example.investplatform.model.enums.Gender;

import java.time.LocalDate;

public record InvestorPrivateEntrepreneurDataDto(
        String lastName,
        String firstName,
        String patronymic,
        Gender gender,
        String citizenship,
        LocalDate birthDate,
        String birthPlace,
        String idDocType,
        String idDocSeries,
        String idDocNumber,
        LocalDate idDocIssuedDate,
        String idDocIssuedBy,
        String idDocDepartmentCode,
        String registrationAddress,
        String residentialAddress,
        String inn,
        String snils,
        String ogrnip,
        String foreignRegistrationInfo,
        String contactEmail,
        String phone
) {
}
