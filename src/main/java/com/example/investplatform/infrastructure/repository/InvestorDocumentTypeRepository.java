package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.investor.InvestorDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestorDocumentTypeRepository extends JpaRepository<InvestorDocumentType, Integer> {
    Optional<InvestorDocumentType> findByCode(String code);
}
