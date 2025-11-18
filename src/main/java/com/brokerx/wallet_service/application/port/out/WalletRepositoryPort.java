package com.brokerx.wallet_service.application.port.out;

import com.brokerx.wallet_service.domain.model.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepositoryPort {

    /* Save a wallet */
    Wallet save(Wallet wallet);

    /* Save multiple wallets */
    List<Wallet> saveAll(List<Wallet> wallets);

    /* Find a wallet by ID */
    Optional<Wallet> findById(Long id);

    /* Find a wallet by user ID */
    Optional<Wallet> findByUserId(Long userId);
}