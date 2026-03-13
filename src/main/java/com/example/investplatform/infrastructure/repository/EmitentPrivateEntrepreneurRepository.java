package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.emitent.EmitentPrivateEntrepreneur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmitentPrivateEntrepreneurRepository extends JpaRepository<EmitentPrivateEntrepreneur, Long> {
    Optional<EmitentPrivateEntrepreneur> findByEmitentId(Long emitentId);
}
