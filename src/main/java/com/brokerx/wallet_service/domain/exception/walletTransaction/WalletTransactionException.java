package com.brokerx.wallet_service.domain.exception.walletTransaction;

import java.math.BigDecimal;

public class WalletTransactionException extends RuntimeException {
    private final String code; // e.g. TX_INVALID_DATA, TX_UNSUPPORTED_TYPE, TX_INSUFFICIENT_FUNDS
    private final String field;
    private final String value;
    private final BigDecimal currentBalance;
    private final BigDecimal attemptedAmount;

    private WalletTransactionException(String code, String field, String value, String message,
            BigDecimal currentBalance, BigDecimal attemptedAmount) {
        super(message);
        this.code = code;
        this.field = field;
        this.value = value;
        this.currentBalance = currentBalance;
        this.attemptedAmount = attemptedAmount;
    }

    public static WalletTransactionException invalid(String field, String value, String message) {
        return new WalletTransactionException("TX_INVALID_DATA", field, value, message, null, null);
    }

    public static WalletTransactionException unsupportedType(String value) {
        return new WalletTransactionException("TX_UNSUPPORTED_TYPE", "type", value,
                "Unsupported transaction type: " + value, null, null);
    }

    public static WalletTransactionException insufficientFunds(BigDecimal current, BigDecimal attempted) {
        return new WalletTransactionException("TX_INSUFFICIENT_FUNDS", "balance",
                current != null ? current.toPlainString() : null,
                "Insufficient balance: current=" + current + ", attempted debit=" + attempted, current, attempted);
    }

    public static WalletTransactionException creditBelowMin(BigDecimal amount, BigDecimal min) {
        return new WalletTransactionException("TX_CREDIT_MIN", "amount", amount.toPlainString(),
                "Credit amount must be at least " + min, null, amount);
    }

    public static WalletTransactionException creditAboveMax(BigDecimal amount, BigDecimal max) {
        return new WalletTransactionException("TX_CREDIT_MAX", "amount", amount.toPlainString(),
                "Credit amount must not exceed " + max, null, amount);
    }

    public static WalletTransactionException amountNotPositive(BigDecimal amount) {
        return new WalletTransactionException("TX_AMOUNT_NOT_POSITIVE", "amount",
                amount != null ? amount.toPlainString() : null, "Amount must be positive", null, amount);
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getAttemptedAmount() {
        return attemptedAmount;
    }
}
