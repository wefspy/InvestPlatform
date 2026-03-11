package com.example.investplatform.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProposalLockScheduler {

    private final InvestmentProposalService proposalService;
    private final InvestmentContractService contractService;

    /**
     * Каждую минуту проверяет просроченные блокировки (heartbeat > 5 минут)
     * и возвращает ИП в общий пул модерации (pending).
     */
    @Scheduled(fixedRate = 60_000)
    public void releaseExpiredProposalLocks() {
        int released = proposalService.releaseExpiredLocks();
        if (released > 0) {
            log.info("Освобождено {} ИП с просроченной блокировкой оператора", released);
        }
    }

    /**
     * Каждую минуту проверяет просроченные блокировки ДИ (heartbeat > 5 минут)
     * и возвращает договоры в общий пул модерации (reviewing, locked_by = NULL).
     */
    @Scheduled(fixedRate = 60_000)
    public void releaseExpiredContractLocks() {
        int released = contractService.releaseExpiredLocks();
        if (released > 0) {
            log.info("Освобождено {} ДИ с просроченной блокировкой оператора", released);
        }
    }
}
