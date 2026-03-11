package com.example.investplatform.infrastructure.security.jwt;

import com.example.investplatform.infrastructure.config.property.JwtProperties;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtKeyProvider {
    private final JwtProperties jwtProperties;

    public JwtKeyProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
