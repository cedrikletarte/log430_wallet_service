package com.brokerx.wallet_service.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void shouldCreateCreditTransaction() {
        Wallet wallet = Wallet.builder().id(1L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        assertNotNull(transaction);
        assertEquals(1L, transaction.getWallet().getId());
        assertEquals(TransactionType.CREDIT, transaction.getType());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertEquals(0, new BigDecimal("100.00").compareTo(transaction.getAmount()));
        assertFalse(transaction.isSettled());
        assertNull(transaction.getSettledAt());
    }

    @Test
    void shouldCreateDebitTransaction() {
        Wallet wallet = Wallet.builder().id(2L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("50.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        assertNotNull(transaction);
        assertEquals(2L, transaction.getWallet().getId());
        assertEquals(TransactionType.DEBIT, transaction.getType());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertEquals(0, new BigDecimal("50.00").compareTo(transaction.getAmount()));
        assertFalse(transaction.isSettled());
    }

    @Test
    void shouldCreateSettledTransaction() {
        LocalDate now = LocalDate.now();
        Wallet wallet = Wallet.builder().id(3L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .amount(new BigDecimal("200.00"))
                .createdAt(now)
                .settledAt(now)
                .isSettled(true)
                .build();

        assertTrue(transaction.isSettled());
        assertEquals(TransactionStatus.SETTLED, transaction.getStatus());
        assertNotNull(transaction.getSettledAt());
        assertEquals(now, transaction.getSettledAt());
    }

    @Test
    void shouldCreateFailedTransaction() {
        Wallet wallet = Wallet.builder().id(4L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.FAILED)
                .amount(new BigDecimal("75.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
        assertFalse(transaction.isSettled());
        assertNull(transaction.getSettledAt());
    }

    @Test
    void shouldUpdateTransactionStatus() {
        Wallet wallet = Wallet.builder().id(5L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("150.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertFalse(transaction.isSettled());

        // Mise à jour vers SETTLED
        transaction.setStatus(TransactionStatus.SETTLED);
        transaction.setSettledAt(LocalDate.now());
        transaction.setSettled(true);

        assertEquals(TransactionStatus.SETTLED, transaction.getStatus());
        assertTrue(transaction.isSettled());
        assertNotNull(transaction.getSettledAt());
    }

    @Test
    void shouldHandleDifferentAmounts() {
        Wallet wallet = Wallet.builder().id(6L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction smallTransaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("0.01"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        Transaction largeTransaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("99999.99"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        assertEquals(0, new BigDecimal("0.01").compareTo(smallTransaction.getAmount()));
        assertEquals(0, new BigDecimal("99999.99").compareTo(largeTransaction.getAmount()));
    }

    @Test
    void shouldHandleDecimalPrecision() {
        Wallet wallet = Wallet.builder().id(7L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .amount(new BigDecimal("123.45"))
                .createdAt(LocalDate.now())
                .settledAt(LocalDate.now())
                .isSettled(true)
                .build();

        assertEquals(0, new BigDecimal("123.45").compareTo(transaction.getAmount()));
    }

    @Test
    void shouldHandleAllTransactionStatuses() {
        LocalDate now = LocalDate.now();
        Wallet wallet = Wallet.builder().id(8L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();

        Transaction pending = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("100.00"))
                .createdAt(now)
                .isSettled(false)
                .build();

        Transaction settled = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .amount(new BigDecimal("100.00"))
                .createdAt(now)
                .settledAt(now)
                .isSettled(true)
                .build();

        Transaction failed = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.FAILED)
                .amount(new BigDecimal("100.00"))
                .createdAt(now)
                .isSettled(false)
                .build();

        assertEquals(TransactionStatus.PENDING, pending.getStatus());
        assertEquals(TransactionStatus.SETTLED, settled.getStatus());
        assertEquals(TransactionStatus.FAILED, failed.getStatus());
    }

    @Test
    void shouldHandleBothTransactionTypes() {
        Wallet wallet = Wallet.builder().id(9L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction credit = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        Transaction debit = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("50.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        assertEquals(TransactionType.CREDIT, credit.getType());
        assertEquals(TransactionType.DEBIT, debit.getType());
    }

    @Test
    void shouldSetAndGetAllFields() {
        Transaction transaction = new Transaction();
        Wallet wallet = Wallet.builder().id(10L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        transaction.setId(100L);
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDate.now());
        transaction.setAmount(new BigDecimal("250.00"));
        transaction.setSettled(false);

        assertEquals(100L, transaction.getId());
        assertEquals(10L, transaction.getWallet().getId());
        assertEquals(TransactionType.CREDIT, transaction.getType());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertNotNull(transaction.getCreatedAt());
        assertEquals(0, new BigDecimal("250.00").compareTo(transaction.getAmount()));
        assertFalse(transaction.isSettled());
    }

    @Test
    void shouldTransitionFromPendingToSettled() {
        Wallet wallet = Wallet.builder().id(11L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("300.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        // État initial
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertFalse(transaction.isSettled());
        assertNull(transaction.getSettledAt());

        // Transition vers SETTLED
        transaction.setStatus(TransactionStatus.SETTLED);
        transaction.setSettledAt(LocalDate.now());
        transaction.setSettled(true);

        // État final
        assertEquals(TransactionStatus.SETTLED, transaction.getStatus());
        assertTrue(transaction.isSettled());
        assertNotNull(transaction.getSettledAt());
    }

    @Test
    void shouldTransitionFromPendingToFailed() {
        Wallet wallet = Wallet.builder().id(12L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("400.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        // État initial
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertFalse(transaction.isSettled());

        // Transition vers FAILED
        transaction.setStatus(TransactionStatus.FAILED);

        // État final
        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
        assertFalse(transaction.isSettled());
        assertNull(transaction.getSettledAt());
    }

    @Test
    void shouldHandleSettledTransactionWithSettledDate() {
        LocalDate now = LocalDate.now();
        Wallet wallet = Wallet.builder().id(13L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .amount(new BigDecimal("500.00"))
                .createdAt(now)
                .settledAt(now)
                .isSettled(true)
                .build();

        assertEquals(TransactionStatus.SETTLED, transaction.getStatus());
        assertTrue(transaction.isSettled());
        assertNotNull(transaction.getSettledAt());
        assertEquals(now, transaction.getCreatedAt());
        assertEquals(now, transaction.getSettledAt());
    }

    @Test
    void shouldCreateTransactionWithNoArgConstructor() {
        Transaction transaction = new Transaction();
        
        assertNotNull(transaction);
        assertNull(transaction.getId());
        assertNull(transaction.getWallet());
        assertNull(transaction.getType());
        assertNull(transaction.getStatus());
        assertNull(transaction.getCreatedAt());
        assertNull(transaction.getSettledAt());
        assertNull(transaction.getAmount());
    }

    @Test
    void shouldCreateTransactionWithAllArgsConstructor() {
        LocalDate now = LocalDate.now();
        Wallet wallet = Wallet.builder().id(100L).userId(1L).currency("USD").balance(BigDecimal.ZERO).build();
        
        Transaction transaction = new Transaction(
                1L,
                wallet,
                TransactionType.CREDIT,
                TransactionStatus.SETTLED,
                now,
                now,
                new BigDecimal("600.00"),
                true
        );

        assertEquals(1L, transaction.getId());
        assertEquals(100L, transaction.getWallet().getId());
        assertEquals(TransactionType.CREDIT, transaction.getType());
        assertEquals(TransactionStatus.SETTLED, transaction.getStatus());
        assertEquals(now, transaction.getCreatedAt());
        assertEquals(now, transaction.getSettledAt());
        assertEquals(0, new BigDecimal("600.00").compareTo(transaction.getAmount()));
        assertTrue(transaction.isSettled());
    }
}
