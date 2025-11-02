package com.brokerx.wallet_service.application.service;

import com.brokerx.wallet_service.application.port.in.useCase.OrderWalletUseCase;
import com.brokerx.wallet_service.application.port.out.WalletRepositoryPort;
import com.brokerx.wallet_service.application.port.out.TransactionRepositoryPort;
import com.brokerx.wallet_service.application.port.out.PositionRepositoryPort;
import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.domain.model.Position;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.model.TransactionStatus;
import com.brokerx.wallet_service.domain.service.WalletValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling order-related wallet operations
 * This implements the use case defined in the application layer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderWalletService implements OrderWalletUseCase {

    private final WalletRepositoryPort walletRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final PositionRepositoryPort positionRepositoryPort;

    @Override
    @Transactional
    public void reserveFundsForWallet(Long walletId, BigDecimal amount) {
        log.info("Reserving funds for order: walletId={}, amount={}", walletId, amount);
        
        Wallet wallet = walletRepositoryPort.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for walletId: " + walletId));
        
        // Move funds from available to reserved
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient available balance");
        }
        
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setReservedBalance(wallet.getReservedBalance().add(amount));
        
        WalletValidator.validateUpdate(wallet);
        walletRepositoryPort.save(wallet);
        
        // Create a pending transaction
        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.PENDING)
                .amount(amount)
                .createdAt(Instant.now())
                .isSettled(false)
                .wallet(wallet)
                .build();
        
        transactionRepositoryPort.save(transaction);
        
        log.info("✅ Reserved {}, new available: {}, reserved: {}", 
                amount, wallet.getAvailableBalance(), wallet.getReservedBalance());
    }

    @Override
    @Transactional
    public void refundCancelledOrder(Long walletId, BigDecimal amount, Long orderId) {
        log.info("Processing refund for cancelled order: walletId={}, amount={}, orderId={}", 
                walletId, amount, orderId);
        
        Wallet wallet = walletRepositoryPort.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for walletId: " + walletId));
        
        // Release reserved funds back to available balance
        wallet.setReservedBalance(wallet.getReservedBalance().subtract(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        
        WalletValidator.validateUpdate(wallet);
        walletRepositoryPort.save(wallet);
        
        // Create a credit transaction for audit trail
        Transaction transaction = Transaction.builder()
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .amount(amount)
                .createdAt(Instant.now())
                .settledAt(Instant.now())
                .isSettled(true)
                .wallet(wallet)
                .build();
        
        transactionRepositoryPort.save(transaction);
        
        log.info("✅ Refunded {} for cancelled order {}, new available: {}, reserved: {}",
                amount, orderId, wallet.getAvailableBalance(), wallet.getReservedBalance());
    }

    @Override
    @Transactional
    public void executeOrder(Long userId, String stockSymbol, String side, Integer quantity, BigDecimal price, Long orderId) {
        log.info("Executing order in wallet: userId={}, symbol={}, side={}, qty={}, price={}, orderId={}",
                userId, stockSymbol, side, quantity, price, orderId);
        
        Wallet wallet = walletRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));
        
        BigDecimal amount = price.multiply(BigDecimal.valueOf(quantity));
        
        if ("BUY".equals(side)) {
            // For BUY: release reserved funds
            wallet.setReservedBalance(wallet.getReservedBalance().subtract(amount));
            
            // Create settled debit transaction
            Transaction transaction = Transaction.builder()
                    .type(TransactionType.DEBIT)
                    .status(TransactionStatus.SETTLED)
                    .amount(amount)
                    .createdAt(Instant.now())
                    .settledAt(Instant.now())
                    .isSettled(true)
                    .wallet(wallet)
                    .build();
            transactionRepositoryPort.save(transaction);

            if(positionRepositoryPort.existsByWalletIdAndSymbol(wallet.getId(), stockSymbol)) {
                Position existingPosition = positionRepositoryPort.findByWalletIdAndSymbol(wallet.getId(), stockSymbol).orElseThrow( () -> new IllegalArgumentException("Position not found"));
                existingPosition.setQuantity(existingPosition.getQuantity() + quantity);
                existingPosition.setTotalCost(existingPosition.getTotalCost().add(amount));
                positionRepositoryPort.save(existingPosition);
            } else {
                Position newPosition = Position.builder()
                        .wallet(wallet)
                        .symbol(stockSymbol)
                        .quantity(quantity)
                        .totalCost(amount)
                        .build();
                positionRepositoryPort.save(newPosition);
            }
            
        } else if ("SELL".equals(side)) {
            // For SELL: credit the wallet
            wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
            
            // Create settled credit transaction
                Transaction transaction = Transaction.builder()
                        .type(TransactionType.CREDIT)
                        .status(TransactionStatus.SETTLED)
                        .amount(amount)
                        .createdAt(Instant.now())
                        .settledAt(Instant.now())
                        .isSettled(true)
                        .wallet(wallet)
                        .build();
                transactionRepositoryPort.save(transaction);

                Position existingPosition = positionRepositoryPort.findByWalletIdAndSymbol(wallet.getId(), stockSymbol)
                                .orElseThrow(() -> new IllegalArgumentException("Insufficient shares to sell"));

                if (existingPosition.getQuantity() < quantity) {
                        throw new IllegalArgumentException("Insufficient shares to sell");
                }

                existingPosition.setQuantity(existingPosition.getQuantity() - quantity);
                
                // Calculate proportional cost to remove
                BigDecimal costPerShare = existingPosition.getTotalCost().divide(
                    BigDecimal.valueOf(existingPosition.getQuantity() + quantity), 
                    10, 
                    RoundingMode.HALF_UP
                );
                BigDecimal costToRemove = costPerShare.multiply(BigDecimal.valueOf(quantity));
                existingPosition.setTotalCost(existingPosition.getTotalCost().subtract(costToRemove));
                
                positionRepositoryPort.save(existingPosition);
        }
        
        WalletValidator.validateUpdate(wallet);
        walletRepositoryPort.save(wallet);
        
        log.info("✅ Order {} executed in wallet, new available: {}, reserved: {}",
                orderId, wallet.getAvailableBalance(), wallet.getReservedBalance());
    }

    @Override
    @Transactional
    public void settleMatchedOrder(Long walletId, String side, String stockSymbol,
                                   Integer quantity, BigDecimal executionPrice,
                                   BigDecimal totalAmount, Long orderId) {
        log.info("Settling matched order: walletId={}, side={}, symbol={}, qty={} @ {}, total={}, orderId={}",
                walletId, side, stockSymbol, quantity, executionPrice, totalAmount, orderId);
        
        Wallet wallet = walletRepositoryPort.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for walletId: " + walletId));
        
        if ("BUY".equals(side)) {
            // For BUY: release reserved funds and add shares to position
            wallet.setReservedBalance(wallet.getReservedBalance().subtract(totalAmount));
            
            // Create settled debit transaction
            Transaction transaction = Transaction.builder()
                    .type(TransactionType.DEBIT)
                    .status(TransactionStatus.SETTLED)
                    .amount(totalAmount)
                    .createdAt(Instant.now())
                    .settledAt(Instant.now())
                    .isSettled(true)
                    .wallet(wallet)
                    .build();
            transactionRepositoryPort.save(transaction);
            
            log.info("💸 BUY order settled: released {} from reserved balance, added {} shares of {}",
                    totalAmount, quantity, stockSymbol);
            
        } else if ("SELL".equals(side)) {
            // For SELL: credit the wallet and remove shares from position
            wallet.setAvailableBalance(wallet.getAvailableBalance().add(totalAmount));
            
            // Create settled credit transaction
            Transaction transaction = Transaction.builder()
                    .type(TransactionType.CREDIT)
                    .status(TransactionStatus.SETTLED)
                    .amount(totalAmount)
                    .createdAt(Instant.now())
                    .settledAt(Instant.now())
                    .isSettled(true)
                    .wallet(wallet)
                    .build();
            transactionRepositoryPort.save(transaction);
            
            log.info("💰 SELL order settled: credited {} to available balance, removed {} shares of {}",
                    totalAmount, quantity, stockSymbol);
        }
        
        WalletValidator.validateUpdate(wallet);
        walletRepositoryPort.save(wallet);
        
        log.info("✅ Matched order {} settled in wallet, new available: {}, reserved: {}",
                orderId, wallet.getAvailableBalance(), wallet.getReservedBalance());
    }
}
