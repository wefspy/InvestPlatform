package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.investor.InvestorLegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestorLegalEntityRepository extends JpaRepository<InvestorLegalEntity, Long> {
}
