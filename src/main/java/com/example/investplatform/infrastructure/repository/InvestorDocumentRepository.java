package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.investor.InvestorDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestorDocumentRepository extends JpaRepository<InvestorDocument, Long> {
    List<InvestorDocument> findByInvestorId(Long investorId);

    Optional<InvestorDocument> findByIdAndInvestorId(Long id, Long investorId);
}
