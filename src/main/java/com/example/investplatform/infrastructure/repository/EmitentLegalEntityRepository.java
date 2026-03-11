package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.emitent.EmitentLegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmitentLegalEntityRepository extends JpaRepository<EmitentLegalEntity, Long> {
}
