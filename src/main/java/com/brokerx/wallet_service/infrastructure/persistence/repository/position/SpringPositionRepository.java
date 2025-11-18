package com.brokerx.wallet_service.infrastructure.persistence.repository.position;

import com.brokerx.wallet_service.infrastructure.persistence.entity.PositionEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringPositionRepository extends JpaRepository<PositionEntity, Long> {

    /* Find a position by wallet ID and stock symbol */
    Optional<PositionEntity> findByWalletIdAndSymbol(Long walletId, String symbol);

    /* Find all positions for a wallet */
    List<PositionEntity> findByWalletId(Long walletId);

    /* Check if a position exists */
    boolean existsByWalletIdAndSymbol(Long walletId, String symbol);
}
