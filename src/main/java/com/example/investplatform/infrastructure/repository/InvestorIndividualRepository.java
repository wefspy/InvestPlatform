package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.investor.InvestorIndividual;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestorIndividualRepository extends JpaRepository<InvestorIndividual, Long> {
}
