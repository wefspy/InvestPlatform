package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.contract.ContractStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractStatusHistoryRepository extends JpaRepository<ContractStatusHistory, Long> {
}
