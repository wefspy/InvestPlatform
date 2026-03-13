package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.registry.RegistryOperationBasis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistryOperationBasisRepository extends JpaRepository<RegistryOperationBasis, Long> {

    @Query("""
            SELECT rob FROM RegistryOperationBasis rob
            JOIN FETCH rob.contractType
            WHERE rob.registryOperation.id IN :operationIds
            """)
    List<RegistryOperationBasis> findByRegistryOperationIdIn(
            @Param("operationIds") List<Long> operationIds);
}
