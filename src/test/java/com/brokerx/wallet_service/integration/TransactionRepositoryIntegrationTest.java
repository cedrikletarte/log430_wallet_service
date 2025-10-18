package com.brokerx.wallet_service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.domain.model.TransactionStatus;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.infrastructure.persistence.mapper.TransactionMapper;
import com.brokerx.wallet_service.infrastructure.persistence.repository.walletTransaction.TransactionRepositoryAdapter;

@Testcontainers
@DataJpaTest
@Import({TransactionRepositoryAdapter.class, TransactionMapper.class})
class TransactionRepositoryIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private TransactionRepositoryAdapter transactionRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldSaveAndRetrieveTransaction() {
        Wallet wallet = Wallet.builder().id(1L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDate.now())
                .amount(BigDecimal.valueOf(100.0))
                .isSettled(false)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        assertNotNull(saved.getId());
        Transaction found = transactionRepository.findById(saved.getId()).orElseThrow();
        assertEquals(1L, found.getWallet().getId().longValue());
        assertEquals(TransactionType.CREDIT, found.getType());
        assertEquals(TransactionStatus.PENDING, found.getStatus());
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(found.getAmount()));
        assertFalse(found.isSettled());
    }

    @Test
    void shouldFindTransactionsByWalletId() {
        // Créer plusieurs transactions pour le même wallet
        Wallet wallet2 = Wallet.builder().id(2L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        Wallet wallet3 = Wallet.builder().id(3L).userId(2L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction1 = Transaction.builder()
                .wallet(wallet2)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .createdAt(LocalDate.now())
                .settledAt(LocalDate.now())
                .amount(BigDecimal.valueOf(200.0))
                .isSettled(true)
                .build();

        Transaction transaction2 = Transaction.builder()
                .wallet(wallet2)
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDate.now())
                .amount(BigDecimal.valueOf(50.0))
                .isSettled(false)
                .build();

        Transaction transaction3 = Transaction.builder()
                .wallet(wallet3) // Différent wallet
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .createdAt(LocalDate.now())
                .settledAt(LocalDate.now())
                .amount(BigDecimal.valueOf(300.0))
                .isSettled(true)
                .build();

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);

        List<Transaction> wallet2Transactions = transactionRepository.findByWalletId(2L);
        
        assertEquals(2, wallet2Transactions.size());
        assertTrue(wallet2Transactions.stream()
                .allMatch(t -> t.getWallet().getId().equals(2L)));
    }

    @Test
    void shouldSaveCreditTransaction() {
        Wallet wallet = Wallet.builder().id(4L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction creditTransaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .createdAt(LocalDate.now())
                .settledAt(LocalDate.now())
                .amount(BigDecimal.valueOf(500.0))
                .isSettled(true)
                .build();

        Transaction saved = transactionRepository.save(creditTransaction);

        assertNotNull(saved.getId());
        Transaction found = transactionRepository.findById(saved.getId()).orElseThrow();
        assertEquals(TransactionType.CREDIT, found.getType());
        assertEquals(TransactionStatus.SETTLED, found.getStatus());
        assertTrue(found.isSettled());
        assertNotNull(found.getSettledAt());
    }

    @Test
    void shouldSaveDebitTransaction() {
        Wallet wallet = Wallet.builder().id(5L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction debitTransaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDate.now())
                .amount(BigDecimal.valueOf(150.0))
                .isSettled(false)
                .build();

        Transaction saved = transactionRepository.save(debitTransaction);

        assertNotNull(saved.getId());
        Transaction found = transactionRepository.findById(saved.getId()).orElseThrow();
        assertEquals(TransactionType.DEBIT, found.getType());
        assertEquals(TransactionStatus.PENDING, found.getStatus());
        assertFalse(found.isSettled());
    }

    @Test
    void shouldUpdateTransactionStatus() {
        Wallet wallet = Wallet.builder().id(6L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDate.now())
                .amount(BigDecimal.valueOf(250.0))
                .isSettled(false)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        // Mettre à jour le statut
        saved.setStatus(TransactionStatus.SETTLED);
        saved.setSettledAt(LocalDate.now());
        saved.setSettled(true);
        transactionRepository.save(saved);

        Transaction updated = transactionRepository.findById(saved.getId()).orElseThrow();
        assertEquals(TransactionStatus.SETTLED, updated.getStatus());
        assertTrue(updated.isSettled());
        assertNotNull(updated.getSettledAt());
    }

    @Test
    void shouldHandleFailedTransaction() {
        Wallet wallet = Wallet.builder().id(7L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.FAILED)
                .createdAt(LocalDate.now())
                .amount(BigDecimal.valueOf(75.0))
                .isSettled(false)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        Transaction found = transactionRepository.findById(saved.getId()).orElseThrow();
        assertEquals(TransactionStatus.FAILED, found.getStatus());
        assertFalse(found.isSettled());
    }
}
