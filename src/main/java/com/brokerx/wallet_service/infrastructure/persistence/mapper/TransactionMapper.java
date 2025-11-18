package com.brokerx.wallet_service.infrastructure.persistence.mapper;

import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.infrastructure.persistence.entity.TransactionEntity;

import org.springframework.stereotype.Component;

/* Mapper for converting between Transaction and TransactionEntity */
@Component
public class TransactionMapper {

    private final WalletMapper walletMapper;

    public TransactionMapper(WalletMapper walletMapper) {
        this.walletMapper = walletMapper;
    }

    public TransactionEntity toEntity(Transaction transaction) {
        if (transaction == null)
            return null;
        return TransactionEntity.builder()
                .id(transaction.getId())
                .wallet(walletMapper.toEntity(transaction.getWallet()))
                .type(transaction.getType())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .settledAt(transaction.getSettledAt())
                .amount(transaction.getAmount())
                .isSettled(transaction.isSettled())
                .orderId(transaction.getOrderId())
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
                .orderId(entity.getOrderId())
                .build();
    }
}
