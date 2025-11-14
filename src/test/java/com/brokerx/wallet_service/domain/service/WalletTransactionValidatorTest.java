package com.brokerx.wallet_service.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.brokerx.wallet_service.domain.exception.transaction.TransactionException;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.model.Wallet;

class WalletTransactionValidatorTest {

    private Wallet createWalletWithBalance(String balance) {
        return Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(new BigDecimal(balance))
                .reservedBalance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void shouldValidateCreditTransaction() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("100.00");

        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, amount, "USD"));
    }

    @Test
    void shouldValidateDebitTransaction() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("500.00");

        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.DEBIT, amount, "USD"));
    }

    @Test
    void shouldRejectNullWallet() {
        BigDecimal amount = new BigDecimal("100.00");

        TransactionException exception = assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        null, TransactionType.CREDIT, amount, "USD"));
        assertTrue(exception.getMessage().contains("Wallet is required"));
    }

    @Test
    void shouldRejectNullTransactionType() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("100.00");

        TransactionException exception = assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, null, amount, "USD"));
        assertTrue(exception.getMessage().contains("Transaction type is required"));
    }

    @Test
    void shouldRejectNullCurrency() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("100.00");

        TransactionException exception = assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.CREDIT, amount, null));
        assertTrue(exception.getMessage().contains("Currency is required"));
    }

    @Test
    void shouldRejectEmptyCurrency() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("100.00");

        TransactionException exception = assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.CREDIT, amount, "   "));
        assertTrue(exception.getMessage().contains("Currency is required"));
    }

    @Test
    void shouldRejectNullAmount() {
        Wallet wallet = createWalletWithBalance("1000.00");

        TransactionException exception = assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.CREDIT, null, "USD"));
        assertTrue(exception.getMessage().contains("Amount is required"));
    }

    @Test
    void shouldRejectZeroAmount() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = BigDecimal.ZERO;

        assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.CREDIT, amount, "USD"));
    }

    @Test
    void shouldRejectNegativeAmount() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("-100.00");

        assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.CREDIT, amount, "USD"));
    }

    @Test
    void shouldRejectCreditBelowMinimum() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("5.00"); // Below MIN_CREDIT of 10.00

        assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.CREDIT, amount, "USD"));
    }

    @Test
    void shouldAcceptCreditAtMinimum() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("10.00"); // Exactly MIN_CREDIT

        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, amount, "USD"));
    }

    @Test
    void shouldRejectCreditAboveMaximum() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("15000.00"); // Above MAX_CREDIT of 10000.00

        assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.CREDIT, amount, "USD"));
    }

    @Test
    void shouldAcceptCreditAtMaximum() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("10000.00"); // Exactly MAX_CREDIT

        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, amount, "USD"));
    }

    @Test
    void shouldAcceptCreditWithinRange() {
        Wallet wallet = createWalletWithBalance("1000.00");
        
        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, new BigDecimal("50.00"), "USD"));
        
        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, new BigDecimal("500.00"), "USD"));
        
        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, new BigDecimal("5000.00"), "USD"));
    }

    @Test
    void shouldRejectDebitWithInsufficientFunds() {
        Wallet wallet = createWalletWithBalance("100.00");
        BigDecimal amount = new BigDecimal("200.00");

        assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.DEBIT, amount, "USD"));
    }

    @Test
    void shouldAcceptDebitWithExactBalance() {
        Wallet wallet = createWalletWithBalance("500.00");
        BigDecimal amount = new BigDecimal("500.00");

        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.DEBIT, amount, "USD"));
    }

    @Test
    void shouldAcceptDebitWithSufficientFunds() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("750.00");

        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.DEBIT, amount, "USD"));
    }

    @Test
    void shouldRejectDebitFromWalletWithNullBalance() {
        Wallet wallet = createWalletWithBalance("1000.00");
        wallet.setAvailableBalance(null);
        BigDecimal amount = new BigDecimal("100.00");

        TransactionException exception = assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.DEBIT, amount, "USD"));
        assertTrue(exception.getMessage().contains("Wallet balance is missing"));
    }

    @Test
    void shouldRejectDebitFromEmptyWallet() {
        Wallet wallet = createWalletWithBalance("0.00");
        BigDecimal amount = new BigDecimal("10.00");

        assertThrows(TransactionException.class,
                () -> WalletTransactionValidator.validateCreation(
                        wallet, TransactionType.DEBIT, amount, "USD"));
    }

    @Test
    void shouldHandleDecimalAmounts() {
        Wallet wallet = createWalletWithBalance("1000.50");
        
        // Credit avec décimales
        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, new BigDecimal("50.75"), "USD"));
        
        // Debit avec décimales
        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.DEBIT, new BigDecimal("250.25"), "USD"));
    }

    @Test
    void shouldValidateDifferentCurrencies() {
        Wallet wallet = createWalletWithBalance("1000.00");
        BigDecimal amount = new BigDecimal("100.00");
        
        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, amount, "USD"));
        
        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, amount, "CAD"));
        
        assertDoesNotThrow(() -> WalletTransactionValidator.validateCreation(
                wallet, TransactionType.CREDIT, amount, "EUR"));
    }
}
