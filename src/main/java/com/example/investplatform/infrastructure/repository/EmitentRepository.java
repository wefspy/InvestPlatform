package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.emitent.Emitent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmitentRepository extends JpaRepository<Emitent, Long> {

    @EntityGraph(attributePaths = {"personalAccount"})
    Optional<Emitent> findByUserId(Long userId);
}
