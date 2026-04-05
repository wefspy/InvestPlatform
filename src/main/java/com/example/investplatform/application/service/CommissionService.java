package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.contract.CommissionInfoDto;
import com.example.investplatform.infrastructure.config.property.CommissionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionProperties commissionProperties;

    public CommissionInfoDto getInfo() {
        return new CommissionInfoDto(
                commissionProperties.getRate(),
                commissionProperties.getMinAmount(),
                commissionProperties.getMaxAmount()
        );
    }

    public BigDecimal calculate(BigDecimal investmentAmount) {
        BigDecimal commission = investmentAmount
                .multiply(commissionProperties.getRate())
                .setScale(2, RoundingMode.HALF_UP);

        if (commissionProperties.getMinAmount() != null
                && commission.compareTo(commissionProperties.getMinAmount()) < 0) {
            commission = commissionProperties.getMinAmount();
        }

        if (commissionProperties.getMaxAmount() != null
                && commission.compareTo(commissionProperties.getMaxAmount()) > 0) {
            commission = commissionProperties.getMaxAmount();
        }

        return commission;
    }
}
