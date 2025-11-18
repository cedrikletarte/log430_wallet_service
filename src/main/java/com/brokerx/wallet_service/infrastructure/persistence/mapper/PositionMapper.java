package com.brokerx.wallet_service.infrastructure.persistence.mapper;

import com.brokerx.wallet_service.domain.model.Position;
import com.brokerx.wallet_service.infrastructure.persistence.entity.PositionEntity;

import org.springframework.stereotype.Component;

/* Mapper for converting between Position and PositionEntity */
@Component
public class PositionMapper {

    private final WalletMapper walletMapper;

    public PositionMapper(WalletMapper walletMapper) {
        this.walletMapper = walletMapper;
    }

    public PositionEntity toEntity(Position position) {
        if (position == null)
            return null;
        return PositionEntity.builder()
                .id(position.getId())
                .wallet(walletMapper.toEntity(position.getWallet()))
                .symbol(position.getSymbol())
                .quantity(position.getQuantity())
                .totalCost(position.getTotalCost())
                .build();
                
    }

    public Position toDomain(PositionEntity entity) {
        if (entity == null)
            return null;
        return Position.builder()
                .id(entity.getId())
                .wallet(walletMapper.toDomain(entity.getWallet()))
                .symbol(entity.getSymbol())
                .quantity(entity.getQuantity())
                .totalCost(entity.getTotalCost())
                .build();
    }
}
