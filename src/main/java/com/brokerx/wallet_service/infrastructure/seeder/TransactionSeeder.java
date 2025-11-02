package com.brokerx.wallet_service.infrastructure.seeder;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.domain.model.TransactionStatus;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.infrastructure.persistence.repository.wallet.WalletRepositoryAdapter;
import com.brokerx.wallet_service.infrastructure.persistence.repository.walletTransaction.TransactionRepositoryAdapter;

@Configuration
@Order(2)
public class TransactionSeeder {

    private static final Logger log = LoggerFactory.getLogger(TransactionSeeder.class);

    @Bean
    CommandLineRunner seedTransaction(
            TransactionRepositoryAdapter walletTransactionRepositoryAdapter,
            WalletRepositoryAdapter walletRepositoryAdapter) {
        return args -> {
            if (walletTransactionRepositoryAdapter.findById(1L).isEmpty()) {
                Wallet wallet = walletRepositoryAdapter.findByUserId(1L)
                        .orElseThrow(() -> new RuntimeException("Wallet with ID 1 not found"));
                
                Transaction transaction = Transaction.builder()
                        .amount(BigDecimal.valueOf(1500))
                        .wallet(wallet)
                        .status(TransactionStatus.SETTLED)
                        .isSettled(true)
                        .type(TransactionType.CREDIT)
                        .settledAt(Instant.ofEpochSecond(1672537600)) // 2023-01-01T00:00:00Z
                        .createdAt(Instant.ofEpochSecond(1672537600)) // 2023-01-01T00:00:00Z
                        .build();
                walletTransactionRepositoryAdapter.save(transaction);
                log.info("Transaction created for wallet ID {}: {}", transaction.getWallet().getId(), transaction.getAmount());
            }
            if(walletTransactionRepositoryAdapter.findById(2L).isEmpty()) {
                Wallet wallet = walletRepositoryAdapter.findByUserId(1L)
                        .orElseThrow(() -> new RuntimeException("Wallet with ID 1 not found"));
                
                Transaction transaction = Transaction.builder()
                        .amount(BigDecimal.valueOf(500))
                        .wallet(wallet)
                        .status(TransactionStatus.SETTLED)
                        .isSettled(true)
                        .type(TransactionType.DEBIT)
                        .settledAt(Instant.ofEpochSecond(1672624000)) // 2023-01-02T00:00:00Z
                        .createdAt(Instant.ofEpochSecond(1672624000)) // 2023-01-02T00:00:00Z
                        .build();
                walletTransactionRepositoryAdapter.save(transaction);
                log.info("Transaction created for wallet ID {}: {}", transaction.getWallet().getId(), transaction.getAmount());
            }
        };
    }
}
