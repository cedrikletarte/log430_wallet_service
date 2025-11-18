package com.brokerx.wallet_service.infrastructure.persistence.repository.wallet;

import com.brokerx.wallet_service.infrastructure.persistence.entity.WalletEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringWalletRepository extends JpaRepository<WalletEntity, Long> {

    /* Find a wallet by user ID */
    Optional<WalletEntity> findByUserId(Long userId);
}
