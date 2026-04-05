package com.example.investplatform.infrastructure.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.commission")
public class CommissionProperties {
    private BigDecimal rate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}
