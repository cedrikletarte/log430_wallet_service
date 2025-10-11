package com.brokerx.wallet_service.application.port.out;

import com.brokerx.wallet_service.domain.model.Transaction;

import java.util.List;

public interface TransactionRepositoryPort {
    Transaction save(Transaction walletTransaction);

    List<Transaction> findByWalletId(Long walletId);

    java.util.Optional<Transaction> findById(Long id);
}
