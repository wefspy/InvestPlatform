package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.proposal.ProposalDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalDocumentTypeRepository extends JpaRepository<ProposalDocumentType, Integer> {
    Optional<ProposalDocumentType> findByCode(String code);
}
