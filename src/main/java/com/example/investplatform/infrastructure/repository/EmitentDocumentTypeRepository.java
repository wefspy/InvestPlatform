package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.emitent.EmitentDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmitentDocumentTypeRepository extends JpaRepository<EmitentDocumentType, Integer> {
    Optional<EmitentDocumentType> findByCode(String code);
}
