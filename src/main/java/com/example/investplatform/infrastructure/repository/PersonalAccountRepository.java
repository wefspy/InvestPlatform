package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.account.PersonalAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalAccountRepository extends JpaRepository<PersonalAccount, Long> {
    Optional<PersonalAccount> findByAccountNumber(String accountNumber);
}
