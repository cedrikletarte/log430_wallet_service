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
        Transaction transaction = Transaction.builder()
                .walletId(1L)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        assertNotNull(transaction);
        assertEquals(1L, transaction.getWalletId());
        assertEquals(TransactionType.CREDIT, transaction.getType());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertEquals(0, new BigDecimal("100.00").compareTo(transaction.getAmount()));
        assertFalse(transaction.isSettled());
        assertNull(transaction.getSettledAt());
    }

    @Test
    void shouldCreateDebitTransaction() {
        Transaction transaction = Transaction.builder()
                .walletId(2L)
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("50.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        assertNotNull(transaction);
        assertEquals(2L, transaction.getWalletId());
        assertEquals(TransactionType.DEBIT, transaction.getType());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertEquals(0, new BigDecimal("50.00").compareTo(transaction.getAmount()));
        assertFalse(transaction.isSettled());
    }

    @Test
    void shouldCreateSettledTransaction() {
        LocalDate now = LocalDate.now();
        Transaction transaction = Transaction.builder()
                .walletId(3L)
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
        Transaction transaction = Transaction.builder()
                .walletId(4L)
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
        Transaction transaction = Transaction.builder()
                .walletId(5L)
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
        Transaction smallTransaction = Transaction.builder()
                .walletId(6L)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("0.01"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        Transaction largeTransaction = Transaction.builder()
                .walletId(6L)
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
        Transaction transaction = Transaction.builder()
                .walletId(7L)
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

        Transaction pending = Transaction.builder()
                .walletId(8L)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("100.00"))
                .createdAt(now)
                .isSettled(false)
                .build();

        Transaction settled = Transaction.builder()
                .walletId(8L)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SETTLED)
                .amount(new BigDecimal("100.00"))
                .createdAt(now)
                .settledAt(now)
                .isSettled(true)
                .build();

        Transaction failed = Transaction.builder()
                .walletId(8L)
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
        Transaction credit = Transaction.builder()
                .walletId(9L)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.PENDING)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDate.now())
                .isSettled(false)
                .build();

        Transaction debit = Transaction.builder()
                .walletId(9L)
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
        
        transaction.setId(100L);
        transaction.setWalletId(10L);
        transaction.setType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDate.now());
        transaction.setAmount(new BigDecimal("250.00"));
        transaction.setSettled(false);

        assertEquals(100L, transaction.getId());
        assertEquals(10L, transaction.getWalletId());
        assertEquals(TransactionType.CREDIT, transaction.getType());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertNotNull(transaction.getCreatedAt());
        assertEquals(0, new BigDecimal("250.00").compareTo(transaction.getAmount()));
        assertFalse(transaction.isSettled());
    }

    @Test
    void shouldTransitionFromPendingToSettled() {
        Transaction transaction = Transaction.builder()
                .walletId(11L)
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
        Transaction transaction = Transaction.builder()
                .walletId(12L)
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
        Transaction transaction = Transaction.builder()
                .walletId(13L)
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
        assertNull(transaction.getWalletId());
        assertNull(transaction.getType());
        assertNull(transaction.getStatus());
        assertNull(transaction.getCreatedAt());
        assertNull(transaction.getSettledAt());
        assertNull(transaction.getAmount());
    }

    @Test
    void shouldCreateTransactionWithAllArgsConstructor() {
        LocalDate now = LocalDate.now();
        Transaction transaction = new Transaction(
                1L,
                100L,
                TransactionType.CREDIT,
                TransactionStatus.SETTLED,
                now,
                now,
                new BigDecimal("600.00"),
                true
        );

        assertEquals(1L, transaction.getId());
        assertEquals(100L, transaction.getWalletId());
        assertEquals(TransactionType.CREDIT, transaction.getType());
        assertEquals(TransactionStatus.SETTLED, transaction.getStatus());
        assertEquals(now, transaction.getCreatedAt());
        assertEquals(now, transaction.getSettledAt());
        assertEquals(0, new BigDecimal("600.00").compareTo(transaction.getAmount()));
        assertTrue(transaction.isSettled());
    }
}
