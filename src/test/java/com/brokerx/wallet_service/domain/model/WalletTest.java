package com.brokerx.wallet_service.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class WalletTest {

    @Test
    void shouldCreditWalletBalance() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(new BigDecimal("1000.00"))
                .reservedBalance(BigDecimal.ZERO)
                .build();

        wallet.credit(new BigDecimal("500.00"));

        assertEquals(0, new BigDecimal("1500.00").compareTo(wallet.getAvailableBalance()));
    }

    @Test
    void shouldDebitWalletBalance() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(new BigDecimal("1000.00"))
                .reservedBalance(BigDecimal.ZERO)
                .build();

        wallet.debit(new BigDecimal("300.00"));

        assertEquals(0, new BigDecimal("700.00").compareTo(wallet.getAvailableBalance()));
    }

    @Test
    void shouldHandleMultipleCredits() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(new BigDecimal("100.00"))
                .reservedBalance(BigDecimal.ZERO)
                .build();

        wallet.credit(new BigDecimal("50.00"));
        wallet.credit(new BigDecimal("25.00"));
        wallet.credit(new BigDecimal("125.00"));

        assertEquals(0, new BigDecimal("300.00").compareTo(wallet.getAvailableBalance()));
    }

    @Test
    void shouldHandleMultipleDebits() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(new BigDecimal("1000.00"))
                .reservedBalance(BigDecimal.ZERO)
                .build();

        wallet.debit(new BigDecimal("100.00"));
        wallet.debit(new BigDecimal("200.00"));
        wallet.debit(new BigDecimal("50.00"));

        assertEquals(0, new BigDecimal("650.00").compareTo(wallet.getAvailableBalance()));
    }

    @Test
    void shouldHandleMixedOperations() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(new BigDecimal("500.00"))
                .reservedBalance(BigDecimal.ZERO)
                .build();

        wallet.credit(new BigDecimal("200.00"));  // 700
        wallet.debit(new BigDecimal("150.00"));   // 550
        wallet.credit(new BigDecimal("50.00"));   // 600
        wallet.debit(new BigDecimal("100.00"));   // 500

        assertEquals(0, new BigDecimal("500.00").compareTo(wallet.getAvailableBalance()));
    }

    @Test
    void shouldHandleDecimalPrecision() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(new BigDecimal("100.55"))
                .reservedBalance(BigDecimal.ZERO)
                .build();

        wallet.credit(new BigDecimal("50.45"));

        assertEquals(0, new BigDecimal("151.00").compareTo(wallet.getAvailableBalance()));
    }

    @Test
    void shouldDebitToZero() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(new BigDecimal("500.00"))
                .reservedBalance(BigDecimal.ZERO)
                .build();

        wallet.debit(new BigDecimal("500.00"));

        assertEquals(0, new BigDecimal("0.00").compareTo(wallet.getAvailableBalance()));
    }

    @Test
    void shouldStartWithZeroBalance() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(BigDecimal.ZERO)
                .reservedBalance(BigDecimal.ZERO)
                .build();

        assertEquals(0, BigDecimal.ZERO.compareTo(wallet.getAvailableBalance()));
    }

    @Test
    void shouldCreditFromZero() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId(100L)
                .currency("USD")
                .availableBalance(BigDecimal.ZERO)
                .reservedBalance(BigDecimal.ZERO)
                .build();

        wallet.credit(new BigDecimal("250.00"));

        assertEquals(0, new BigDecimal("250.00").compareTo(wallet.getAvailableBalance()));
    }
}
