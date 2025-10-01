package com.brokerx.wallet_service.infrastructure.persistence.mapper;

import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.infrastructure.persistence.entity.WalletTransactionEntity;

import org.springframework.stereotype.Component;

@Component
public class WalletTransactionMapper {

    public WalletTransactionEntity toEntity(Transaction walletTransaction) {
        if (walletTransaction == null)
            return null;
        return WalletTransactionEntity.builder()
                .id(walletTransaction.getId())
                .walletId(walletTransaction.getWalletId())
                .type(walletTransaction.getType())
                .status(walletTransaction.getStatus())
                .createdAt(walletTransaction.getCreatedAt())
                .settledAt(walletTransaction.getSettledAt())
                .currency(walletTransaction.getCurrency())
                .amount(walletTransaction.getAmount())
                .isSettled(walletTransaction.isSettled())
                .build();
    }

    public Transaction toDomain(WalletTransactionEntity entity) {
        if (entity == null)
            return null;
        return Transaction.builder()
                .id(entity.getId())
                .walletId(entity.getWalletId())
                .type(entity.getType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .settledAt(entity.getSettledAt())
                .currency(entity.getCurrency())
                .amount(entity.getAmount())
                .isSettled(entity.isSettled())
                .build();
    }
}
