package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.registry.RegistryOperationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistryOperationDocumentRepository extends JpaRepository<RegistryOperationDocument, Long> {

    List<RegistryOperationDocument> findByRegistryOperationId(Long registryOperationId);

    List<RegistryOperationDocument> findByRegistryOperationIdIn(List<Long> registryOperationIds);
}
