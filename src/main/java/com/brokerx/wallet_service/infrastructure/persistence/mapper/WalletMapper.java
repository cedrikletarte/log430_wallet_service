package com.brokerx.wallet_service.infrastructure.persistence.mapper;

import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.infrastructure.persistence.entity.WalletEntity;

import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletEntity toEntity(Wallet wallet) {
        if (wallet == null)
            return null;
        return WalletEntity.builder()
                .id(wallet.getId())
                .availableBalance(wallet.getAvailableBalance())
                .reservedBalance(wallet.getReservedBalance())
                .currency(wallet.getCurrency())
                .userId(wallet.getUserId())
                .build();
    }

    public Wallet toDomain(WalletEntity entity) {
        if (entity == null)
            return null;
        return Wallet.builder()
                .id(entity.getId())
                .availableBalance(entity.getAvailableBalance())
                .reservedBalance(entity.getReservedBalance())
                .currency(entity.getCurrency())
                .userId(entity.getUserId())
                .build();
    }
}
