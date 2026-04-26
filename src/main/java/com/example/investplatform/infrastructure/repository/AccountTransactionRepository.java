package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.account.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    Page<AccountTransaction> findByPersonalAccountId(Long personalAccountId, Pageable pageable);

    @EntityGraph(attributePaths = "payment")
    Page<AccountTransaction> findWithPaymentByPersonalAccountId(Long personalAccountId, Pageable pageable);
}
