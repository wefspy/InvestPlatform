package com.example.investplatform.application.dto.emitent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmitentLegalEntityDto(
        @NotBlank(message = "Полное наименование обязательное поле")
        String fullName,

        String shortName,

        @NotBlank(message = "ОГРН обязательное поле")
        @Size(min = 13, max = 13, message = "ОГРН должен содержать 13 символов")
        String ogrn,

        @NotBlank(message = "ИНН обязательное поле")
        @Size(min = 10, max = 10, message = "ИНН юрлица должен содержать 10 символов")
        String inn,

        String kpp,

        @NotBlank(message = "Юридический адрес обязательное поле")
        String legalAddress,

        String postalAddress,

        String okpo,

        String okato,

        String organisationForm,

        String materialFacts
) {
}
