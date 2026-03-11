package com.example.investplatform.infrastructure.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Objects;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private Duration accessExpiration;
    private Duration refreshExpiration;
}
