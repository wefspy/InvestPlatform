package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.investor.InvestorPrivateEntrepreneur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestorPrivateEntrepreneurRepository extends JpaRepository<InvestorPrivateEntrepreneur, Long> {
    Optional<InvestorPrivateEntrepreneur> findByInvestorId(Long investorId);
}
