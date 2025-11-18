package com.brokerx.wallet_service.infrastructure.persistence.repository.walletTransaction;

import com.brokerx.wallet_service.infrastructure.persistence.mapper.TransactionMapper;
import com.brokerx.wallet_service.application.port.out.TransactionRepositoryPort;
import com.brokerx.wallet_service.domain.model.Transaction;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

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

    /* Save a wallet transaction */
    @Override
    public Transaction save(Transaction walletTransaction) {
        var entity = walletTransactionMapper.toEntity(walletTransaction);
        entity = springWalletRepository.save(entity);
        return walletTransactionMapper.toDomain(entity);
    }

    /* Find all transactions for a wallet */
    @Override
    public List<Transaction> findByWalletId(Long walletId) {
        var entities = springWalletRepository.findByWalletId(walletId);
        return entities.stream()
                .map(walletTransactionMapper::toDomain)
                .toList();
    }

    /* Find a transaction by its ID */
    @Override
    public Optional<Transaction> findById(Long id) {
        var entity = springWalletRepository.findById(id);
        return entity.map(walletTransactionMapper::toDomain);
    }

    /* Find a transaction by order ID */
    @Override
    public Transaction findByOrderId(Long orderId) {
        var entity = springWalletRepository.findByOrderId(orderId);
        return walletTransactionMapper.toDomain(entity);
    }
}
