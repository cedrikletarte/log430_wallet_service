package com.brokerx.wallet_service.infrastructure.persistence.repository.walletTransaction;

import com.brokerx.wallet_service.infrastructure.persistence.entity.TransactionEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringTransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByWalletId(Long walletId);

    TransactionEntity findByOrderId(Long orderId);
}
