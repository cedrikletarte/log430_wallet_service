package com.brokerx.wallet_service.infrastructure.persistence.repository.walletTransaction;

import com.brokerx.wallet_service.infrastructure.persistence.entity.TransactionEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringTransactionRepository extends JpaRepository<TransactionEntity, Long> {

    /* Find all transactions for a wallet */
    List<TransactionEntity> findByWalletId(Long walletId);

    /* Find a transaction by order ID */
    TransactionEntity findByOrderId(Long orderId);
}
