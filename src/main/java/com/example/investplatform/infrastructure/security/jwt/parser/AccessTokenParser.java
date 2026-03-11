package com.example.investplatform.infrastructure.security.jwt.parser;

import com.example.investplatform.infrastructure.security.jwt.JwtKeyProvider;
import com.example.investplatform.infrastructure.security.jwt.dto.AccessTokenPayload;
import com.example.investplatform.infrastructure.security.jwt.enums.JwtClaims;
import com.example.investplatform.infrastructure.security.jwt.enums.JwtTokenType;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AccessTokenParser extends AbstractJwtTokenParser {
    private final JwtTokenType jwtTokenType = JwtTokenType.ACCESS;

    public AccessTokenParser(JwtKeyProvider jwtKeyProvider) {
        super(jwtKeyProvider);
    }

    public String getUsername(String token) {
        Claims claims = getClaims(token);
        return getUsername(claims);
    }

    public Set<String> getRoles(String token) {
        Claims claims = getClaims(token);
        return getRoles(claims);
    }

    @Override
    public AccessTokenPayload getPayload(String token) {
        Claims claims = getClaims(token);
        return new AccessTokenPayload(
                getTokenId(claims),
                getUserId(claims),
                getTokenType(claims),
                getUsername(claims),
                getRoles(claims)
        );
    }

    @Override
    public Boolean ofType(String token) {
        return ofType(token, jwtTokenType);
    }

    protected String getUsername(Claims claims) {
        assertType(claims, jwtTokenType);
        String key = JwtClaims.USERNAME.getKey();
        Object obj = getClaim(key, claims);

        return String.valueOf(obj);
    }

    protected Set<String> getRoles(Claims claims) {
        assertType(claims, jwtTokenType);
        String key = JwtClaims.ROLES.getKey();

        Object obj = getClaim(key, claims);
        List<?> roles = (ArrayList<?>) obj;
        return roles.stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());
    }
}
