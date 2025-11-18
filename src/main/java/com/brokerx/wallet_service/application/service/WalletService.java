package com.brokerx.wallet_service.application.service;

import com.brokerx.wallet_service.application.port.in.command.PositionSuccess;
import com.brokerx.wallet_service.application.port.in.command.TransactionSuccess;
import com.brokerx.wallet_service.application.port.in.command.WalletSuccess;
import com.brokerx.wallet_service.application.port.in.useCase.WalletUseCase;
import com.brokerx.wallet_service.application.port.out.WalletRepositoryPort;
import com.brokerx.wallet_service.application.port.out.PositionRepositoryPort;
import com.brokerx.wallet_service.application.port.out.TransactionRepositoryPort;
import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.domain.model.Position;
import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.model.TransactionStatus;
import com.brokerx.wallet_service.domain.service.WalletTransactionValidator;
import com.brokerx.wallet_service.domain.service.WalletValidator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class WalletService implements WalletUseCase {

    private static final Logger logger = LogManager.getLogger(WalletService.class);

    private final WalletRepositoryPort walletRepositoryPort;
    private final TransactionRepositoryPort walletTransactionRepositoryPort;
    private final PositionRepositoryPort positionRepositoryPort;

    public WalletService(WalletRepositoryPort walletRepositoryPort,
            TransactionRepositoryPort walletTransactionRepositoryPort,
            PositionRepositoryPort positionRepositoryPort) {
        this.walletRepositoryPort = walletRepositoryPort;
        this.walletTransactionRepositoryPort = walletTransactionRepositoryPort;
        this.positionRepositoryPort = positionRepositoryPort;
    }

    /* Get or create wallet for a user */
    private synchronized Wallet getOrCreateWallet(Long userId) {
        return walletRepositoryPort.findByUserId(userId)
                .orElseGet(() -> {
                    // Double-check after acquiring lock
                    return walletRepositoryPort.findByUserId(userId)
                            .orElseGet(() -> {
                                logger.info("Wallet not found for userId: {}, creating new wallet", userId);
                                Wallet created = Wallet.builder()
                                        .userId(userId)
                                        .currency("USD")
                                        .availableBalance(BigDecimal.ZERO)
                                        .reservedBalance(BigDecimal.ZERO)
                                        .build();
                                WalletValidator.validateCreation(created);
                                return walletRepositoryPort.save(created);
                            });
                });
    }

    /* Debit amount from user's wallet */
    @Override
    public void debit(Long userId, BigDecimal amount) {
        logger.info("Debit request - UserId: {}, Amount: {}", userId, amount);
        
        validateAmount(amount);
        Wallet wallet = getOrCreateWallet(userId);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.SETTLED)
                .amount(amount)
                .createdAt(Instant.now())
                .settledAt(Instant.now())
                .isSettled(true)
                .wallet(wallet)
                .build();

        WalletTransactionValidator.validateCreation(wallet, TransactionType.DEBIT, amount, wallet.getCurrency());
        walletTransactionRepositoryPort.save(transaction);

        wallet.debit(amount);
        WalletValidator.validateUpdate(wallet);

        walletRepositoryPort.save(wallet);
        
        logger.info("Debit successful - UserId: {}, Amount: {}, New balance: {}", userId, amount, wallet.getAvailableBalance());
    }

    /* Credit amount to user's wallet */
    @Override
    public void credit(Long userId, BigDecimal amount) {
        logger.info("Credit request - UserId: {}, Amount: {}", userId, amount);
        
        validateAmount(amount);
        Wallet wallet = getOrCreateWallet(userId);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .amount(amount)
                .createdAt(Instant.now())
                .settledAt(Instant.now())
                .isSettled(true)
                .wallet(wallet)
                .build();

        WalletTransactionValidator.validateCreation(wallet, TransactionType.CREDIT, amount, wallet.getCurrency());
        walletTransactionRepositoryPort.save(transaction);

        wallet.credit(amount);
        WalletValidator.validateUpdate(wallet);
        walletRepositoryPort.save(wallet);
        
        logger.info("Credit successful - UserId: {}, Amount: {}, New balance: {}", userId, amount, wallet.getAvailableBalance());
    }

    /* Get wallet details by user ID */
    @Override
    public WalletSuccess getWalletByUserId(Long userId) {
        logger.info("Get wallet request - UserId: {}", userId);
        
        Wallet wallet = getOrCreateWallet(userId);

        return WalletSuccess.builder()
                .id(wallet.getId())
                .availableBalance(wallet.getAvailableBalance())
                .reservedBalance(wallet.getReservedBalance())
                .currency(wallet.getCurrency())
                .build();
    }

    /* Get transactions by user ID */
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

    /* Get positions by user ID */
    @Override
    public List<PositionSuccess> getPositionsByUserId(Long userId) {
        Wallet wallet = walletRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));

        List<Position> positions = positionRepositoryPort.findByWalletId(wallet.getId());

        return positions.stream()
                .map(pos -> PositionSuccess.builder()
                        .id(pos.getId())
                        .symbol(pos.getSymbol())
                        .quantity(pos.getQuantity())
                        .totalCost(pos.getTotalCost())
                        .build())
                .toList();
    }

    /* Validate that the amount is positive */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Invalid amount provided: {}", amount);
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
