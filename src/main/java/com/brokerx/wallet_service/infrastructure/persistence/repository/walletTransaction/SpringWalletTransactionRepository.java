package com.brokerx.wallet_service.infrastructure.persistence.repository.walletTransaction;

import com.brokerx.wallet_service.infrastructure.persistence.entity.WalletTransactionEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringWalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long> {

    List<WalletTransactionEntity> findByWalletId(Long walletId);
}
