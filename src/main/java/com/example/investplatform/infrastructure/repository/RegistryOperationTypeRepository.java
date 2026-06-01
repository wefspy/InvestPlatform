package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.registry.RegistryOperationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistryOperationTypeRepository extends JpaRepository<RegistryOperationType, Long> {

    Optional<RegistryOperationType> findByCode(String code);
}
