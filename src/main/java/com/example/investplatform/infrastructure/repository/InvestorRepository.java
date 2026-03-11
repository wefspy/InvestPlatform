package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.investor.Investor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestorRepository extends JpaRepository<Investor, Long> {

    @EntityGraph(attributePaths = {"personalAccount"})
    Optional<Investor> findByUserId(Long userId);
}
