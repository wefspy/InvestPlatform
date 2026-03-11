package com.example.investplatform.application.dto.emitent;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateEmitentPrivateEntrepreneurDto(
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

        @NotNull(message = "Дата рождения обязательное поле")
        LocalDate birthDate,

        @NotBlank(message = "Место рождения обязательное поле")
        String birthPlace,

        @NotBlank(message = "ОГРНИП обязательное поле")
        @Size(min = 15, max = 15, message = "ОГРНИП должен содержать 15 символов")
        String ogrnip,

        @NotBlank(message = "ИНН обязательное поле")
        @Size(min = 12, max = 12, message = "ИНН ИП должен содержать 12 символов")
        String inn,

        @NotBlank(message = "Адрес регистрации обязательное поле")
        String registrationAddress,

        @NotBlank(message = "СНИЛС обязательное поле")
        @Size(min = 11, max = 11, message = "СНИЛС должен содержать 11 символов")
        String snils,

        String materialFacts
) {
}
