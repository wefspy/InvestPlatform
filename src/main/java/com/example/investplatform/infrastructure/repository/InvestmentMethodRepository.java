package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.proposal.InvestmentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestmentMethodRepository extends JpaRepository<InvestmentMethod, Integer> {
    Optional<InvestmentMethod> findByCode(String code);
}
