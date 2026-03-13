package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.registry.RegistryOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RegistryOperationRepository extends JpaRepository<RegistryOperation, Long> {

    @Query("""
            SELECT ro FROM RegistryOperation ro
            JOIN FETCH ro.operationType
            LEFT JOIN FETCH ro.accountTransfer at
            LEFT JOIN FETCH at.accountType
            LEFT JOIN FETCH ro.accountReceive ar
            LEFT JOIN FETCH ar.accountType
            LEFT JOIN FETCH ro.security s
            LEFT JOIN FETCH s.securityClassification
            LEFT JOIN FETCH s.securityCategory
            WHERE ro.dateState BETWEEN :dateFrom AND :dateTo
            ORDER BY ro.processingDatetime ASC
            """)
    List<RegistryOperation> findAllByDateStateBetween(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo);
}
