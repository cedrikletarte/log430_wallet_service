package com.brokerx.wallet_service.application.port.out;

import com.brokerx.wallet_service.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {
    Transaction save(Transaction walletTransaction);

    List<Transaction> findByWalletId(Long walletId);

    Optional<Transaction> findById(Long id);

    Transaction findByOrderId(Long orderId);
}
