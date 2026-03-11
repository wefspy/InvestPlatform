package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.user.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperatorRepository extends JpaRepository<Operator, Long> {
    Optional<Operator> findByUserId(Long userId);
}
