package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.contract.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractStatusRepository extends JpaRepository<ContractStatus, Integer> {
    Optional<ContractStatus> findByCode(String code);
}
