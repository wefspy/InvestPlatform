package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.account.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
}
