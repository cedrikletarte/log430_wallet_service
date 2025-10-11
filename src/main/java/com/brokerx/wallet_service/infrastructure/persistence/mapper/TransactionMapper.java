package com.brokerx.wallet_service.infrastructure.persistence.mapper;

import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.infrastructure.persistence.entity.TransactionEntity;

import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionEntity toEntity(Transaction walletTransaction) {
        if (walletTransaction == null)
            return null;
        return TransactionEntity.builder()
                .id(walletTransaction.getId())
                .walletId(walletTransaction.getWalletId())
                .type(walletTransaction.getType())
                .status(walletTransaction.getStatus())
                .createdAt(walletTransaction.getCreatedAt())
                .settledAt(walletTransaction.getSettledAt())
                .amount(walletTransaction.getAmount())
                .isSettled(walletTransaction.isSettled())
                .build();
    }

    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null)
            return null;
        return Transaction.builder()
                .id(entity.getId())
                .walletId(entity.getWalletId())
                .type(entity.getType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .settledAt(entity.getSettledAt())
                .amount(entity.getAmount())
                .isSettled(entity.isSettled())
                .build();
    }
}
