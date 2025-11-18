package com.brokerx.wallet_service.domain.exception.transaction;

import java.math.BigDecimal;

public class TransactionException extends RuntimeException {
    private final String code;
    private final String field;
    private final String value;
    private final BigDecimal currentBalance;
    private final BigDecimal attemptedAmount;

    private TransactionException(String code, String field, String value, String message,
            BigDecimal currentBalance, BigDecimal attemptedAmount) {
        super(message);
        this.code = code;
        this.field = field;
        this.value = value;
        this.currentBalance = currentBalance;
        this.attemptedAmount = attemptedAmount;
    }

    public static TransactionException invalid(String field, String value, String message) {
        return new TransactionException("TX_INVALID_DATA", field, value, message, null, null);
    }

    public static TransactionException unsupportedType(String value) {
        return new TransactionException("TX_UNSUPPORTED_TYPE", "type", value,
                "Unsupported transaction type: " + value, null, null);
    }

    public static TransactionException insufficientFunds(BigDecimal current, BigDecimal attempted) {
        return new TransactionException("TX_INSUFFICIENT_FUNDS", "balance",
                current != null ? current.toPlainString() : null,
                "Insufficient balance: current=" + current + ", attempted debit=" + attempted, current, attempted);
    }

    public static TransactionException creditBelowMin(BigDecimal amount, BigDecimal min) {
        return new TransactionException("TX_CREDIT_MIN", "amount", amount.toPlainString(),
                "Credit amount must be at least " + min, null, amount);
    }

    public static TransactionException creditAboveMax(BigDecimal amount, BigDecimal max) {
        return new TransactionException("TX_CREDIT_MAX", "amount", amount.toPlainString(),
                "Credit amount must not exceed " + max, null, amount);
    }

    public static TransactionException amountNotPositive(BigDecimal amount) {
        return new TransactionException("TX_AMOUNT_NOT_POSITIVE", "amount",
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
