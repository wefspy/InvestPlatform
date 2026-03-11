package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.proposal.ProposalDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalDocumentRepository extends JpaRepository<ProposalDocument, Long> {
    List<ProposalDocument> findByProposalId(Long proposalId);
}
