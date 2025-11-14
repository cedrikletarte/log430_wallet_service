package com.brokerx.wallet_service.domain.service;

import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.domain.exception.transaction.TransactionException;
import com.brokerx.wallet_service.domain.model.TransactionType;

import java.math.BigDecimal;

public class WalletTransactionValidator {

    private static final BigDecimal MIN_CREDIT = new BigDecimal("10.00");
    private static final BigDecimal MAX_CREDIT = new BigDecimal("10000.00");

    public static void validateCreation(Wallet wallet,
            TransactionType type,
            BigDecimal amount,
            String currency) {
        if (wallet == null) {
            throw TransactionException.invalid("wallet", "null", "Wallet is required");
        }
        if (type == null) {
            throw TransactionException.invalid("type", "null", "Transaction type is required");
        }
        if (currency == null || currency.isBlank()) {
            throw TransactionException.invalid("currency", String.valueOf(currency), "Currency is required");
        }
        if (amount == null) {
            throw TransactionException.invalid("amount", "null", "Amount is required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw TransactionException.amountNotPositive(amount);
        }

        switch (type) {
            case CREDIT -> validateCreditAmount(amount);
            case DEBIT -> validateDebitAmount(wallet, amount);
        }
    }

    private static void validateCreditAmount(BigDecimal amount) {
        if (amount.compareTo(MIN_CREDIT) < 0) {
            throw TransactionException.creditBelowMin(amount, MIN_CREDIT);
        }
        if (amount.compareTo(MAX_CREDIT) > 0) {
            throw TransactionException.creditAboveMax(amount, MAX_CREDIT);
        }
    }

    private static void validateDebitAmount(Wallet wallet, BigDecimal amount) {
        if (wallet.getAvailableBalance() == null) {
            throw TransactionException.invalid("balance", "null", "Wallet balance is missing");
        }
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw TransactionException.insufficientFunds(wallet.getAvailableBalance(), amount);
        }
    }
}