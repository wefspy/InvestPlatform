package com.example.investplatform.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /**
     * Раз в час проверяет активные ИП с истёкшим сроком и закрывает их
     * (completed/failed) с каскадом на договоры. Каждое ИП в своей транзакции,
     * чтобы сбой по одному не блокировал остальные.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void closeExpiredProposals() {
        List<Long> ids = proposalService.findExpiredActiveProposalIds();
        if (ids.isEmpty()) {
            return;
        }
        int closed = 0;
        for (Long id : ids) {
            try {
                proposalService.closeExpiredProposal(id);
                closed++;
            } catch (Exception e) {
                log.error("Не удалось автоматически закрыть ИП {}: {}", id, e.getMessage(), e);
            }
        }
        log.info("Автоматически закрыто {} из {} просроченных ИП", closed, ids.size());
    }
}
