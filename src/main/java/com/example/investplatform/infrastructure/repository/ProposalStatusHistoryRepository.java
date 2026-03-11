package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.proposal.ProposalStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalStatusHistoryRepository extends JpaRepository<ProposalStatusHistory, Long> {
    List<ProposalStatusHistory> findByProposalIdOrderByCreatedAtDesc(Long proposalId);
}
