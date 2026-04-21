package com.example.investplatform.application.dto.emitent;

import java.time.LocalDateTime;

public record EmitentDocumentResponseDto(
        Long id,
        String documentTypeCode,
        String documentTypeName,
        String fileName,
        String filePath,
        Long fileSize,
        String mimeType,
        LocalDateTime uploadedAt
) {
}
