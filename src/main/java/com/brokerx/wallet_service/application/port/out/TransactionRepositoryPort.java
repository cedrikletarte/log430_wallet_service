package com.brokerx.wallet_service.application.port.out;

import com.brokerx.wallet_service.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {

    /* Save a transaction */
    Transaction save(Transaction walletTransaction);

    /* Find transactions by wallet ID */
    List<Transaction> findByWalletId(Long walletId);

    /* Find a transaction by ID */
    Optional<Transaction> findById(Long id);

    /* Find a transaction by order ID */
    Transaction findByOrderId(Long orderId);
}
