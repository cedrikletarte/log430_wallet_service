package com.brokerx.wallet_service.application.service;

import com.brokerx.wallet_service.application.port.in.command.TransactionSuccess;
import com.brokerx.wallet_service.application.port.in.command.WalletSuccess;
import com.brokerx.wallet_service.application.port.in.useCase.WalletUseCase;
import com.brokerx.wallet_service.application.port.out.WalletRepositoryPort;
import com.brokerx.wallet_service.application.port.out.TransactionRepositoryPort;
import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.model.TransactionStatus;
import com.brokerx.wallet_service.domain.service.WalletTransactionValidator;
import com.brokerx.wallet_service.domain.service.WalletValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class WalletService implements WalletUseCase {

    private static final Logger logger = LogManager.getLogger(WalletService.class);

    private final WalletRepositoryPort walletRepositoryPort;
    private final TransactionRepositoryPort walletTransactionRepositoryPort;

    public WalletService(WalletRepositoryPort walletRepositoryPort,
            TransactionRepositoryPort walletTransactionRepositoryPort) {
        this.walletRepositoryPort = walletRepositoryPort;
        this.walletTransactionRepositoryPort = walletTransactionRepositoryPort;
    }

    @Override
    public void debit(Long userId, BigDecimal amount) {
        logger.info("Debit request - UserId: {}, Amount: {}", userId, amount);
        
        validateAmount(amount);
        Wallet wallet = walletRepositoryPort.findByUserId(userId)
                .orElseGet(() -> {
                    logger.info("Wallet not found for userId: {}, creating new wallet", userId);
                    Wallet created = Wallet.builder()
                            .userId(userId)
                            .currency("USD")
                            .balance(BigDecimal.ZERO)
                            .build();
                    // Validate creation then persist to obtain an id before debit operation
                    WalletValidator.validateCreation(created);
                    return walletRepositoryPort.save(created);
                });

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.SETTLED)
                .amount(amount)
                .createdAt(LocalDate.now())
                .settledAt(LocalDate.now())
                .isSettled(true)
                .walletId(wallet.getId())
                .build();

        WalletTransactionValidator.validateCreation(wallet, TransactionType.DEBIT, amount, wallet.getCurrency());
        walletTransactionRepositoryPort.save(transaction);

        wallet.debit(amount);
        WalletValidator.validateUpdate(wallet);

        walletRepositoryPort.save(wallet);
        
        logger.info("Debit successful - UserId: {}, Amount: {}, New balance: {}", userId, amount, wallet.getBalance());
    }

    @Override
    public void credit(Long userId, BigDecimal amount) {
        logger.info("Credit request - UserId: {}, Amount: {}", userId, amount);
        
        validateAmount(amount);
        Wallet wallet = walletRepositoryPort.findByUserId(userId)
                .orElseGet(() -> {
                    logger.info("Wallet not found for userId: {}, creating new wallet", userId);
                    Wallet created = Wallet.builder()
                            .userId(userId)
                            .currency("USD")
                            .balance(BigDecimal.ZERO)
                            .build();
                    WalletValidator.validateCreation(created);
                    return walletRepositoryPort.save(created);
                });

        Transaction transaction = Transaction.builder()
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .amount(amount)
                .createdAt(LocalDate.now())
                .settledAt(LocalDate.now())
                .isSettled(true)
                .walletId(wallet.getId())
                .build();

        WalletTransactionValidator.validateCreation(wallet, TransactionType.CREDIT, amount, wallet.getCurrency());
        walletTransactionRepositoryPort.save(transaction);

        wallet.credit(amount);
        WalletValidator.validateUpdate(wallet);
        walletRepositoryPort.save(wallet);
        
        logger.info("Credit successful - UserId: {}, Amount: {}, New balance: {}", userId, amount, wallet.getBalance());
    }

    @Override
    public WalletSuccess getWalletByUserId(Long userId) {
        logger.info("Get wallet request - UserId: {}", userId);
        
        Wallet wallet = walletRepositoryPort.findByUserId(userId)
                .orElseGet(() -> {
                    logger.info("Wallet not found for userId: {}, creating new wallet", userId);
                    Wallet created = Wallet.builder()
                            .userId(userId)
                            .currency("USD")
                            .balance(BigDecimal.ZERO)
                            .build();
                    WalletValidator.validateCreation(created);
                    return walletRepositoryPort.save(created);
                });

        return WalletSuccess.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }

    @Override
    public List<TransactionSuccess> getTransactionsByUserId(Long userId) {
        Wallet wallet = walletRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));

        List<Transaction> transactions = walletTransactionRepositoryPort.findByWalletId(wallet.getId());

        return transactions.stream()
                .map(tx -> TransactionSuccess.builder()
                        .id(tx.getId())
                        .type(tx.getType())
                        .status(tx.getStatus())
                        .createdAt(tx.getCreatedAt())
                        .settledAt(tx.getSettledAt())
                        .amount(tx.getAmount())
                        .isSettled(tx.isSettled())
                        .build())
                .toList();
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Invalid amount provided: {}", amount);
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
