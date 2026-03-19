package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.account.AccountBalanceDto;
import com.example.investplatform.application.dto.account.AccountTransactionDto;
import com.example.investplatform.infrastructure.repository.AccountTransactionRepository;
import com.example.investplatform.infrastructure.repository.EmitentRepository;
import com.example.investplatform.infrastructure.repository.InvestorRepository;
import com.example.investplatform.model.entity.account.PersonalAccount;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final InvestorRepository investorRepository;
    private final EmitentRepository emitentRepository;
    private final AccountTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public AccountBalanceDto getBalance(Long userId) {
        PersonalAccount account = findPersonalAccount(userId);

        return new AccountBalanceDto(
                account.getAccountNumber(),
                account.getBalance(),
                account.getHoldAmount(),
                account.getBalance().subtract(account.getHoldAmount())
        );
    }

    @Transactional(readOnly = true)
    public Page<AccountTransactionDto> getTransactions(Long userId, Pageable pageable) {
        PersonalAccount account = findPersonalAccount(userId);

        return transactionRepository.findByPersonalAccountId(account.getId(), pageable)
                .map(tx -> new AccountTransactionDto(
                        tx.getId(),
                        tx.getTransactionType(),
                        tx.getAmount(),
                        tx.getBalanceAfter(),
                        tx.getDescription(),
                        tx.getCreatedAt()
                ));
    }

    private PersonalAccount findPersonalAccount(Long userId) {
        return investorRepository.findByUserId(userId)
                .map(investor -> investor.getPersonalAccount())
                .or(() -> emitentRepository.findByUserId(userId)
                        .map(emitent -> emitent.getPersonalAccount()))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Лицевой счёт для пользователя с ID %d не найден".formatted(userId)));
    }
}
