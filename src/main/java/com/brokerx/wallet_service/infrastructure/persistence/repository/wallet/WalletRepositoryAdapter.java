package com.brokerx.wallet_service.infrastructure.persistence.repository.wallet;

import com.brokerx.wallet_service.application.port.out.WalletRepositoryPort;
import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.infrastructure.persistence.entity.WalletEntity;
import com.brokerx.wallet_service.infrastructure.persistence.mapper.WalletMapper;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class WalletRepositoryAdapter implements WalletRepositoryPort {

    private final SpringWalletRepository springWalletRepository;
    private final WalletMapper walletMapper;

    public WalletRepositoryAdapter(SpringWalletRepository springWalletRepository, WalletMapper walletMapper) {
        this.springWalletRepository = springWalletRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = walletMapper.toEntity(wallet);
        entity = springWalletRepository.save(entity);
        return walletMapper.toDomain(entity);
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return springWalletRepository.findById(id)
                .map(walletMapper::toDomain);
    }

    @Override
    public Optional<Wallet> findByUserId(Long userId) {
        return springWalletRepository.findByUserId(userId)
                .map(walletMapper::toDomain);
    }

}
