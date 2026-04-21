package com.example.investplatform.infrastructure.security.jwt.parser;

import com.example.investplatform.infrastructure.security.jwt.JwtKeyProvider;
import com.example.investplatform.infrastructure.security.jwt.enums.JwtTokenType;
import org.springframework.stereotype.Component;

@Component
public class TwoFactorTokenParser extends AbstractJwtTokenParser {
    private final JwtTokenType jwtTokenType = JwtTokenType.TWO_FACTOR;

    protected TwoFactorTokenParser(JwtKeyProvider jwtKeyProvider) {
        super(jwtKeyProvider);
    }

    @Override
    public Boolean ofType(String token) {
        return ofType(token, jwtTokenType);
    }
}
