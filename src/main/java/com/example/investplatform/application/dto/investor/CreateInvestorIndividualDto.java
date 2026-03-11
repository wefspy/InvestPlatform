package com.example.investplatform.application.dto.investor;

import com.example.investplatform.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInvestorIndividualDto(
        @NotBlank(message = "Email обязательное поле")
        @Email(message = "Email не валидный")
        String email,

        @NotBlank(message = "Пароль обязательное поле")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password,

        @NotBlank(message = "Фамилия обязательное поле")
        String lastName,

        @NotBlank(message = "Имя обязательное поле")
        String firstName,

        String patronymic,

        @NotNull(message = "Пол обязательное поле")
        Gender gender,

        @NotBlank(message = "Гражданство обязательное поле")
        String citizenship,

        @NotNull(message = "Дата рождения обязательное поле")
        LocalDate birthDate,

        @NotBlank(message = "Место рождения обязательное поле")
        String birthPlace,

        @NotBlank(message = "Тип документа обязательное поле")
        String idDocType,

        String idDocSeries,

        @NotBlank(message = "Номер документа обязательное поле")
        String idDocNumber,

        @NotNull(message = "Дата выдачи документа обязательное поле")
        LocalDate idDocIssuedDate,

        @NotBlank(message = "Кем выдан документ обязательное поле")
        String idDocIssuedBy,

        String idDocDepartmentCode,

        @NotBlank(message = "Адрес регистрации обязательное поле")
        String registrationAddress,

        @NotBlank(message = "Адрес проживания обязательное поле")
        String residentialAddress,

        String inn,

        String snils,

        @NotBlank(message = "Контактный email обязательное поле")
        @Email(message = "Контактный email не валидный")
        String contactEmail,

        @NotBlank(message = "Телефон обязательное поле")
        String phone,

        BigDecimal investedOtherPlatforms
) {
}
