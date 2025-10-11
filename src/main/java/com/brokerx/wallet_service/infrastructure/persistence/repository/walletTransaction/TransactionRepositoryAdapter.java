package com.brokerx.wallet_service.infrastructure.persistence.repository.walletTransaction;

import com.brokerx.wallet_service.infrastructure.persistence.mapper.TransactionMapper;
import com.brokerx.wallet_service.application.port.out.TransactionRepositoryPort;
import com.brokerx.wallet_service.domain.model.Transaction;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final SpringTransactionRepository springWalletRepository;
    private final TransactionMapper walletTransactionMapper;

    public TransactionRepositoryAdapter(SpringTransactionRepository springWalletRepository,
            TransactionMapper walletTransactionMapper) {
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

    @Override
    public java.util.Optional<Transaction> findById(Long id) {
        var entity = springWalletRepository.findById(id);
        return entity.map(walletTransactionMapper::toDomain);
    }
}
