package com.brokerx.wallet_service.domain.service;

import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.domain.exception.wallet.WalletException;

import java.math.BigDecimal;

public class WalletValidator {

    // Validation during wallet creation
    public static void validateCreation(Wallet wallet) {
        if (wallet == null) {
            throw WalletException.invalid("wallet", "null", "Wallet is null");
        }
        if (wallet.getUserId() == null) {
            throw WalletException.invalid("userId", "null", "Wallet userId is required");
        }
        if (wallet.getCurrency() == null || wallet.getCurrency().isBlank()) {
            throw WalletException.invalid("currency", String.valueOf(wallet.getCurrency()),
                    "Wallet currency is required");
        }
        if (wallet.getAvailableBalance() == null) {
            throw WalletException.invalid("balance", "null", "Wallet balance is required");
        }
        if (wallet.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw WalletException.negativeBalance(wallet.getAvailableBalance().toPlainString());
        }
    }

    // Validation during wallet update (after operation)
    public static void validateUpdate(Wallet wallet) {
        if (wallet == null) {
            throw WalletException.invalid("wallet", "null", "Wallet does not exist");
        }
        if (wallet.getId() == null) {
            throw WalletException.invalid("id", "null", "Wallet id is required for update");
        }
        if (wallet.getAvailableBalance() == null) {
            throw WalletException.invalid("balance", "null", "Wallet balance cannot be null");
        }
        if (wallet.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw WalletException.negativeBalance(wallet.getAvailableBalance().toPlainString());
        }
    }
}