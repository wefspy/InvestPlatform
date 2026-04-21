package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.emitent.EmitentDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmitentDocumentRepository extends JpaRepository<EmitentDocument, Long> {
    List<EmitentDocument> findByEmitentId(Long emitentId);

    Optional<EmitentDocument> findByIdAndEmitentId(Long id, Long emitentId);
}
