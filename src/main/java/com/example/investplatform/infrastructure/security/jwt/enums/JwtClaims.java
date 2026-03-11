package com.example.investplatform.infrastructure.security.jwt.enums;

public enum JwtClaims {
    TOKEN_TYPE("token_type"),
    USERNAME("username"),
    ROLES("roles");

    private final String key;

    JwtClaims(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
