package com.example.investplatform.infrastructure.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.credentials.root")
public class RootProperties {
    private String username;
    private String password;
    private String email;
}
