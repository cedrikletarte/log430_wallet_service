package com.brokerx.wallet_service.application.port.out;

import com.brokerx.wallet_service.domain.model.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepositoryPort {
    Wallet save(Wallet wallet);

    List<Wallet> saveAll(List<Wallet> wallets);

    Optional<Wallet> findById(Long id);

    Optional<Wallet> findByUserId(Long userId);
}