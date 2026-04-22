package com.example.investplatform.application.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DepositRequestDto(
        @NotNull(message = "Сумма не может быть пустой")
        @DecimalMin(value = "1.00", message = "Минимальная сумма пополнения — 1 рубль")
        @Digits(integer = 15, fraction = 2, message = "Сумма должна иметь не более 2 знаков после запятой")
        BigDecimal amount,

        @Size(max = 500, message = "Описание не может быть длиннее 500 символов")
        String description
) {
}
