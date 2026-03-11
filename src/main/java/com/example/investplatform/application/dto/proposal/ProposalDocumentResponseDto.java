package com.example.investplatform.application.dto.proposal;

import java.time.LocalDateTime;

public record ProposalDocumentResponseDto(
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
