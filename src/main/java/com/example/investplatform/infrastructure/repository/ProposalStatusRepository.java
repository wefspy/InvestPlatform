package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.proposal.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalStatusRepository extends JpaRepository<ProposalStatus, Integer> {
    Optional<ProposalStatus> findByCode(String code);
}
