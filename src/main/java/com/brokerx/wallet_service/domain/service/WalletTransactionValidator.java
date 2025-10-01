package com.brokerx.wallet_service.domain.service;

import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.exception.walletTransaction.WalletTransactionException;

import java.math.BigDecimal;

public class WalletTransactionValidator {

    private static final BigDecimal MIN_CREDIT = new BigDecimal("10.00");
    private static final BigDecimal MAX_CREDIT = new BigDecimal("10000.00");

    public static void validateCreation(Wallet wallet,
            TransactionType type,
            BigDecimal amount,
            String currency) {
        if (wallet == null) {
            throw WalletTransactionException.invalid("wallet", "null", "Wallet is required");
        }
        if (type == null) {
            throw WalletTransactionException.invalid("type", "null", "Transaction type is required");
        }
        if (currency == null || currency.isBlank()) {
            throw WalletTransactionException.invalid("currency", String.valueOf(currency), "Currency is required");
        }
        if (amount == null) {
            throw WalletTransactionException.invalid("amount", "null", "Amount is required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw WalletTransactionException.amountNotPositive(amount);
        }

        switch (type) {
            case CREDIT -> validateCreditAmount(amount);
            case DEBIT -> validateDebitAmount(wallet, amount);
        }
    }

    private static void validateCreditAmount(BigDecimal amount) {
        if (amount.compareTo(MIN_CREDIT) < 0) {
            throw WalletTransactionException.creditBelowMin(amount, MIN_CREDIT);
        }
        if (amount.compareTo(MAX_CREDIT) > 0) {
            throw WalletTransactionException.creditAboveMax(amount, MAX_CREDIT);
        }
    }

    private static void validateDebitAmount(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance() == null) {
            throw WalletTransactionException.invalid("balance", "null", "Wallet balance is missing");
        }
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw WalletTransactionException.insufficientFunds(wallet.getBalance(), amount);
        }
    }
}