package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.account.PersonalAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalAccountRepository extends JpaRepository<PersonalAccount, Long> {
}
