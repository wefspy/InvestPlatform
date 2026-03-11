package com.example.investplatform.infrastructure.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.application")
public class AppInfoProperties {
    private String name;
    private String version;
    private String description;
}
