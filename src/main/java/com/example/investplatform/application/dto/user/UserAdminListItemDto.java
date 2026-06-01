package com.example.investplatform.application.dto.user;

import java.time.LocalDateTime;

public record UserAdminListItemDto(
        Long id,
        String email,
        String role,
        String displayName,
        String subtype,
        Boolean isEnabled,
        Boolean isAccountNonLocked,
        Boolean is2faEnabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
