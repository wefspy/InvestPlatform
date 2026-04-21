package com.example.investplatform.application.dto.investor;

import java.time.LocalDateTime;

public record InvestorDocumentResponseDto(
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
