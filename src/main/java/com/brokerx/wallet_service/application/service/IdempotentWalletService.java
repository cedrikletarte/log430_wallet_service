package com.brokerx.wallet_service.application.service;

import com.brokerx.wallet_service.application.port.in.useCase.WalletUseCase;
import com.brokerx.wallet_service.application.port.in.useCase.WalletWithIdempotencyUseCase;
import com.brokerx.wallet_service.application.port.out.IdempotencyCachePort;
import com.brokerx.wallet_service.application.port.in.command.TransactionSuccess;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/* Service implementing idempotent debit/credit for wallet */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotentWalletService implements WalletWithIdempotencyUseCase {

    private final WalletUseCase walletUseCase;
    private final IdempotencyCachePort idempotencyCachePort;

    /* Credit wallet with idempotency check */
    @Override
    public TransactionSuccess creditWithIdempotency(Long userId, BigDecimal amount, String idempotencyKey) {
        log.info("Processing credit with idempotency: userId={}, idempotencyKey={}", userId, idempotencyKey);
        if (idempotencyCachePort.isDuplicate(idempotencyKey, userId)) {
            log.warn("Duplicate credit request detected: idempotencyKey={}, userId={}", idempotencyKey, userId);
            Object cachedResponse = idempotencyCachePort.getCachedResponse(idempotencyKey, userId);
            if (cachedResponse instanceof TransactionSuccess) {
                log.info("Returning cached credit response for idempotency key: {}", idempotencyKey);
                return (TransactionSuccess) cachedResponse;
            }
            return null;
        }
        walletUseCase.credit(userId, amount);
        // On suppose que la dernière transaction est la bonne (à améliorer si besoin)
        TransactionSuccess tx = walletUseCase.getTransactionsByUserId(userId).stream()
            .reduce((first, second) -> second).orElse(null);
        idempotencyCachePort.storeResponse(idempotencyKey, userId, tx);
        return tx;
    }

    /* Debit wallet with idempotency check */
    @Override
    public TransactionSuccess debitWithIdempotency(Long userId, BigDecimal amount, String idempotencyKey) {
        log.info("Processing debit with idempotency: userId={}, idempotencyKey={}", userId, idempotencyKey);
        if (idempotencyCachePort.isDuplicate(idempotencyKey, userId)) {
            log.warn("Duplicate debit request detected: idempotencyKey={}, userId={}", idempotencyKey, userId);
            Object cachedResponse = idempotencyCachePort.getCachedResponse(idempotencyKey, userId);
            if (cachedResponse instanceof TransactionSuccess) {
                log.info("Returning cached debit response for idempotency key: {}", idempotencyKey);
                return (TransactionSuccess) cachedResponse;
            }
            return null;
        }
        walletUseCase.debit(userId, amount);
        TransactionSuccess tx = walletUseCase.getTransactionsByUserId(userId).stream()
            .reduce((first, second) -> second).orElse(null);
        idempotencyCachePort.storeResponse(idempotencyKey, userId, tx);
        return tx;
    }

    /* Check if a request is duplicate based on idempotency key */
    @Override
    public boolean isDuplicateRequest(String idempotencyKey, Long userId) {
        return idempotencyCachePort.isDuplicate(idempotencyKey, userId);
    }

    /* Get cached response for an idempotency key */
    @Override
    public TransactionSuccess getCachedResponse(String idempotencyKey, Long userId) {
        Object cachedResponse = idempotencyCachePort.getCachedResponse(idempotencyKey, userId);
        if (cachedResponse instanceof TransactionSuccess) {
            return (TransactionSuccess) cachedResponse;
        }
        return null;
    }
}
