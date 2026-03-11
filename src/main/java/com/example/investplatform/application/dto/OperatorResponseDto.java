package com.example.investplatform.application.dto;

public record OperatorResponseDto(
        Long id,
        String email,
        String lastName,
        String firstName,
        String patronymic
) {
}
