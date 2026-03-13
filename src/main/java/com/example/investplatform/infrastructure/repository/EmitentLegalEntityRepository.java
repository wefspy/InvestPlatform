package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.emitent.EmitentLegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmitentLegalEntityRepository extends JpaRepository<EmitentLegalEntity, Long> {
    Optional<EmitentLegalEntity> findByEmitentId(Long emitentId);
}
