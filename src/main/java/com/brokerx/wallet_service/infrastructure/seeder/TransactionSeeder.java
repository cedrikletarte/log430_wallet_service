package com.brokerx.wallet_service.infrastructure.seeder;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.domain.model.TransactionStatus;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.infrastructure.persistence.repository.walletTransaction.TransactionRepositoryAdapter;

@Configuration
@Order(2)
public class TransactionSeeder {

        private static final Logger log = LoggerFactory.getLogger(TransactionSeeder.class);

    @Bean
    CommandLineRunner seedTransaction(TransactionRepositoryAdapter walletTransactionRepositoryAdapter) {
        return args -> {
            if (walletTransactionRepositoryAdapter.findById(1L).isEmpty()) {
                Transaction transaction = Transaction.builder()
                        .amount(BigDecimal.valueOf(1500))
                        .walletId(1L)
                        .status(TransactionStatus.SETTLED)
                        .isSettled(true)
                        .type(TransactionType.CREDIT)
                        .settledAt(LocalDate.of(2023, 1, 1))
                        .createdAt(LocalDate.of(2023, 1, 1))
                        .build();
                walletTransactionRepositoryAdapter.save(transaction);
                log.info("Transaction created for wallet ID {}: {}", transaction.getWalletId(), transaction.getAmount());
            }
            if(walletTransactionRepositoryAdapter.findById(2L).isEmpty()) {
                Transaction transaction = Transaction.builder()
                        .amount(BigDecimal.valueOf(250))
                        .walletId(1L)
                        .status(TransactionStatus.SETTLED)
                        .isSettled(true)
                        .type(TransactionType.DEBIT)
                        .settledAt(LocalDate.of(2023, 1, 2))
                        .createdAt(LocalDate.of(2023, 1, 2))
                        .build();
                walletTransactionRepositoryAdapter.save(transaction);
                log.info("Transaction created for wallet ID {}: {}", transaction.getWalletId(), transaction.getAmount());
            }
        };
    }
}
