package com.example.investplatform.infrastructure.security.jwt.parser;

import com.example.investplatform.infrastructure.security.jwt.JwtKeyProvider;
import com.example.investplatform.infrastructure.security.jwt.enums.JwtTokenType;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenParser extends AbstractJwtTokenParser {
    private final JwtTokenType jwtTokenType = JwtTokenType.REFRESH;

    protected RefreshTokenParser(JwtKeyProvider jwtKeyProvider) {
        super(jwtKeyProvider);
    }

    @Override
    public Boolean ofType(String token) {
        return ofType(token, jwtTokenType);
    }
}
