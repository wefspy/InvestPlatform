package com.example.investplatform.application.dto.emitent;

import com.example.investplatform.model.enums.EmitentType;

public record EmitentDetailDto(
        Long id,
        String email,
        EmitentType emitentType,
        String accountNumber,
        EmitentPrivateEntrepreneurDataDto privateEntrepreneur,
        EmitentLegalEntityDataDto legalEntity
) {
}
