package com.brokerx.wallet_service.infrastructure.persistence.mapper;

import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.infrastructure.persistence.entity.TransactionEntity;

import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    private final WalletMapper walletMapper;

    public TransactionMapper(WalletMapper walletMapper) {
        this.walletMapper = walletMapper;
    }

    public TransactionEntity toEntity(Transaction walletTransaction) {
        if (walletTransaction == null)
            return null;
        return TransactionEntity.builder()
                .id(walletTransaction.getId())
                .wallet(walletMapper.toEntity(walletTransaction.getWallet()))
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
                .wallet(walletMapper.toDomain(entity.getWallet()))
                .type(entity.getType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .settledAt(entity.getSettledAt())
                .amount(entity.getAmount())
                .isSettled(entity.isSettled())
                .build();
    }
}
