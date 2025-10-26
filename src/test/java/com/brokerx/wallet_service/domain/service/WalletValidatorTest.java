package com.brokerx.wallet_service.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.brokerx.wallet_service.domain.exception.wallet.WalletException;
import com.brokerx.wallet_service.domain.model.Wallet;

class WalletValidatorTest {

    private Wallet createValidWallet() {
        return Wallet.builder()
                .userId(1L)
                .currency("USD")
                .availableBalance(new BigDecimal("1000.00"))
                .reservedBalance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void shouldValidateWalletForCreation() {
        Wallet wallet = createValidWallet();

        assertDoesNotThrow(() -> WalletValidator.validateCreation(wallet));
    }

    @Test
    void shouldRejectNullWalletForCreation() {
        WalletException exception = assertThrows(WalletException.class,
                () -> WalletValidator.validateCreation(null));
        assertTrue(exception.getMessage().contains("Wallet is null"));
    }

    @Test
    void shouldRejectNullUserIdForCreation() {
        Wallet wallet = createValidWallet();
        wallet.setUserId(null);

        WalletException exception = assertThrows(WalletException.class,
                () -> WalletValidator.validateCreation(wallet));
        assertTrue(exception.getMessage().contains("Wallet userId is required"));
    }

    @Test
    void shouldRejectNullCurrencyForCreation() {
        Wallet wallet = createValidWallet();
        wallet.setCurrency(null);

        WalletException exception = assertThrows(WalletException.class,
                () -> WalletValidator.validateCreation(wallet));
        assertTrue(exception.getMessage().contains("Wallet currency is required"));
    }

    @Test
    void shouldRejectEmptyCurrencyForCreation() {
        Wallet wallet = createValidWallet();
        wallet.setCurrency("   ");

        WalletException exception = assertThrows(WalletException.class,
                () -> WalletValidator.validateCreation(wallet));
        assertTrue(exception.getMessage().contains("Wallet currency is required"));
    }

    @Test
    void shouldRejectNullAvailableBalanceForCreation() {
        Wallet wallet = createValidWallet();
        wallet.setAvailableBalance(null);

        WalletException exception = assertThrows(WalletException.class,
                () -> WalletValidator.validateCreation(wallet));
        assertTrue(exception.getMessage().contains("Wallet availableBalance is required"));
    }

    @Test
    void shouldRejectNegativeAvailableBalanceForCreation() {
        Wallet wallet = createValidWallet();
        wallet.setAvailableBalance(new BigDecimal("-100.00"));

        assertThrows(WalletException.class,
                () -> WalletValidator.validateCreation(wallet));
    }

    @Test
    void shouldAcceptZeroAvailableBalanceForCreation() {
        Wallet wallet = createValidWallet();
        wallet.setAvailableBalance(BigDecimal.ZERO);

        assertDoesNotThrow(() -> WalletValidator.validateCreation(wallet));
    }

    @Test
    void shouldAcceptPositiveBalanceForCreation() {
        Wallet wallet = createValidWallet();

        wallet.setAvailableBalance(new BigDecimal("0.01"));
        assertDoesNotThrow(() -> WalletValidator.validateCreation(wallet));

        wallet.setAvailableBalance(new BigDecimal("1000.00"));
        assertDoesNotThrow(() -> WalletValidator.validateCreation(wallet));

        wallet.setAvailableBalance(new BigDecimal("99999.99"));
        assertDoesNotThrow(() -> WalletValidator.validateCreation(wallet));
    }

    @Test
    void shouldValidateWalletForUpdate() {
        Wallet wallet = createValidWallet();
        wallet.setId(1L);

        assertDoesNotThrow(() -> WalletValidator.validateUpdate(wallet));
    }

    @Test
    void shouldRejectNullWalletForUpdate() {
        WalletException exception = assertThrows(WalletException.class,
                () -> WalletValidator.validateUpdate(null));
        assertTrue(exception.getMessage().contains("Wallet does not exist"));
    }

    @Test
    void shouldRejectNullIdForUpdate() {
        Wallet wallet = createValidWallet();
        wallet.setId(null);

        WalletException exception = assertThrows(WalletException.class,
                () -> WalletValidator.validateUpdate(wallet));
        assertTrue(exception.getMessage().contains("Wallet id is required for update"));
    }

    @Test
    void shouldRejectNullBalanceForUpdate() {
        Wallet wallet = createValidWallet();
        wallet.setId(1L);
        wallet.setAvailableBalance(null);

        WalletException exception = assertThrows(WalletException.class,
                () -> WalletValidator.validateUpdate(wallet));
        assertTrue(exception.getMessage().contains("Wallet availableBalance cannot be null"));
    }

    @Test
    void shouldRejectNegativeBalanceForUpdate() {
        Wallet wallet = createValidWallet();
        wallet.setId(1L);
        wallet.setAvailableBalance(new BigDecimal("-50.00"));

        assertThrows(WalletException.class,
                () -> WalletValidator.validateUpdate(wallet));
    }

    @Test
    void shouldAcceptZeroBalanceForUpdate() {
        Wallet wallet = createValidWallet();
        wallet.setId(1L);
        wallet.setAvailableBalance(BigDecimal.ZERO);

        assertDoesNotThrow(() -> WalletValidator.validateUpdate(wallet));
    }

    @Test
    void shouldAcceptDifferentCurrencies() {
        Wallet wallet = createValidWallet();
        
        wallet.setCurrency("USD");
        assertDoesNotThrow(() -> WalletValidator.validateCreation(wallet));
        
        wallet.setCurrency("CAD");
        assertDoesNotThrow(() -> WalletValidator.validateCreation(wallet));
        
        wallet.setCurrency("EUR");
        assertDoesNotThrow(() -> WalletValidator.validateCreation(wallet));
    }
}
