package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.investor.InvestorLegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestorLegalEntityRepository extends JpaRepository<InvestorLegalEntity, Long> {
    Optional<InvestorLegalEntity> findByInvestorId(Long investorId);
}
