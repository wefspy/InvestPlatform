package com.example.investplatform.infrastructure.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.yookassa")
public class YookassaProperties {
    private String apiUrl;
    private String shopId;
    private String secretKey;
    private String returnUrl;
    private String currency;
    private Duration connectTimeout;
    private Duration readTimeout;
    private List<String> allowedWebhookIps;
}
