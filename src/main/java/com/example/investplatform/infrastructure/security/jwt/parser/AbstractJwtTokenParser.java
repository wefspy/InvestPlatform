package com.example.investplatform.infrastructure.security.jwt.parser;

import com.example.investplatform.application.exception.ClaimNotFoundException;
import com.example.investplatform.application.exception.InvalidJwtTokenTypeException;
import com.example.investplatform.infrastructure.security.jwt.JwtKeyProvider;
import com.example.investplatform.infrastructure.security.jwt.dto.JwtTokenPayload;
import com.example.investplatform.infrastructure.security.jwt.enums.JwtClaims;
import com.example.investplatform.infrastructure.security.jwt.enums.JwtTokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

import java.util.UUID;

public abstract class AbstractJwtTokenParser {
    private final JwtKeyProvider jwtKeyProvider;

    protected AbstractJwtTokenParser(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    public abstract Boolean ofType(String token);

    public JwtTokenPayload getPayload(String token) {
        Claims claims = getClaims(token);
        return new JwtTokenPayload(
                getTokenId(claims),
                getUserId(claims),
                getTokenType(claims)
        );
    }

    public JwtTokenType getTokenType(String token) {
        Claims claims = getClaims(token);
        return getTokenType(claims);
    }

    public UUID getTokenId(String token) {
        Claims claims = getClaims(token);
        return getTokenId(claims);
    }

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return getUserId(claims);
    }

    public Boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (MalformedJwtException
                 | ExpiredJwtException
                 | UnsupportedJwtException
                 | SignatureException
                 | IllegalArgumentException e) {
            return false;
        }
    }

    protected Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtKeyProvider.getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    protected void assertType(Claims claims, JwtTokenType expected) {
        JwtTokenType actual = getTokenType(claims);

        if (!expected.equals(actual)) {
            throw new InvalidJwtTokenTypeException(
                    String.format("Ожидался %s но получили %s", expected, actual)
            );
        }
    }

    protected Boolean ofType(String token, JwtTokenType type) {
        return getTokenType(token).equals(type);
    }

    protected JwtTokenType getTokenType(Claims claims) {
        String key = JwtClaims.TOKEN_TYPE.getKey();
        Object obj = getClaim(key, claims);
        return JwtTokenType.valueOf(obj.toString());
    }

    protected UUID getTokenId(Claims claims) {
        String key = Claims.ID;
        Object obj = getClaim(key, claims);
        return UUID.fromString(obj.toString());
    }

    protected Long getUserId(Claims claims) {
        String key = Claims.SUBJECT;
        Object obj = getClaim(key, claims);
        return Long.valueOf(obj.toString());
    }

    protected Object getClaim(String key, Claims claims) {
        Object obj = claims.get(key);
        if (obj == null) {
            throw new ClaimNotFoundException(String.format("Claim '%s' не найден", key));
        }

        return obj;
    }
}
