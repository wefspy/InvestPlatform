package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.emitent.EmitentDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmitentDocumentRepository extends JpaRepository<EmitentDocument, Long> {
}
