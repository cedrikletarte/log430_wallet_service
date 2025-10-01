package com.brokerx.wallet_service.infrastructure.persistence.repository.walletTransaction;

import com.brokerx.wallet_service.infrastructure.persistence.mapper.WalletTransactionMapper;
import com.brokerx.wallet_service.application.port.out.WalletTransactionRepositoryPort;
import com.brokerx.wallet_service.domain.model.Transaction;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class WalletTransactionRepositoryAdapter implements WalletTransactionRepositoryPort {

    private final SpringWalletTransactionRepository springWalletRepository;
    private final WalletTransactionMapper walletTransactionMapper;

    public WalletTransactionRepositoryAdapter(SpringWalletTransactionRepository springWalletRepository,
            WalletTransactionMapper walletTransactionMapper) {
        this.springWalletRepository = springWalletRepository;
        this.walletTransactionMapper = walletTransactionMapper;
    }

    @Override
    public Transaction save(Transaction walletTransaction) {
        var entity = walletTransactionMapper.toEntity(walletTransaction);
        entity = springWalletRepository.save(entity);
        return walletTransactionMapper.toDomain(entity);
    }

    @Override
    public java.util.List<Transaction> findByWalletId(Long walletId) {
        var entities = springWalletRepository.findByWalletId(walletId);
        return entities.stream()
                .map(walletTransactionMapper::toDomain)
                .toList();
    }
}
