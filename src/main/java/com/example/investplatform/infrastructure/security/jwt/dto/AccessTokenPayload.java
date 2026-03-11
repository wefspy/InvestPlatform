package com.example.investplatform.infrastructure.security.jwt.dto;

import com.example.investplatform.infrastructure.security.jwt.enums.JwtTokenType;
import io.jsonwebtoken.lang.Collections;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AccessTokenPayload extends JwtTokenPayload {
    private final String username;
    private final Set<String> roles;

    public AccessTokenPayload(UUID id,
                              Long userId,
                              JwtTokenType jwtTokenType,
                              String username,
                              Set<String> roles) {
        super(id, userId, jwtTokenType);
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return Collections.immutable(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AccessTokenPayload that = (AccessTokenPayload) o;

        return Objects.equals(username, that.username)
                && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                username,
                roles);
    }

    @Override
    public String toString() {
        return "AccessTokenPayload{" +
                "username='" + username + '\'' +
                ", roles=" + roles +
                ", id=" + id +
                ", userId=" + userId +
                ", tokenType=" + jwtTokenType +
                '}';
    }
}
