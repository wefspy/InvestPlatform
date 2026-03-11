package com.example.investplatform.application.dto;

public record FileResponseDto(
        String fileName,
        String contentType,
        long size
) {
}
