package com.example.investplatform.application.dto.emitent;

import com.example.investplatform.model.enums.EmitentType;

public record EmitentResponseDto(
        Long id,
        String email,
        EmitentType emitentType,
        String accountNumber
) {
}
