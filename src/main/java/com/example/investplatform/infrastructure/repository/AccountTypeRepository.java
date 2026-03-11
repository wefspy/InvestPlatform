package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.account.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountTypeRepository extends JpaRepository<AccountType, Integer> {
    Optional<AccountType> findByCode(String code);
}
