package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.investor.InvestorDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestorDocumentRepository extends JpaRepository<InvestorDocument, Long> {
}
