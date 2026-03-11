package com.example.investplatform.infrastructure.security.jwt.dto;

import com.example.investplatform.infrastructure.security.jwt.enums.JwtTokenType;

import java.util.Objects;
import java.util.UUID;

public class JwtTokenPayload {
    protected final UUID id;
    protected final Long userId;
    protected final JwtTokenType jwtTokenType;

    public JwtTokenPayload(UUID id,
                           Long userId,
                           JwtTokenType jwtTokenType) {
        this.id = id;
        this.userId = userId;
        this.jwtTokenType = jwtTokenType;
    }

    public UUID getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public JwtTokenType getJwtTokenType() {
        return jwtTokenType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JwtTokenPayload that = (JwtTokenPayload) o;

        return Objects.equals(getId(), that.getId())
                && Objects.equals(getUserId(), that.getUserId())
                && getJwtTokenType() == that.getJwtTokenType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getUserId(),
                getJwtTokenType());
    }

    @Override
    public String toString() {
        return "JwtTokenPayload{" +
                "id=" + id +
                ", userId=" + userId +
                ", tokenType=" + jwtTokenType +
                '}';
    }
}
