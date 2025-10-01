package com.brokerx.wallet_service.domain.exception.wallet;

public class WalletException extends RuntimeException {
    private final String code; // e.g. WALLET_INVALID_DATA, WALLET_NEGATIVE_BALANCE
    private final String field;
    private final String value;

    private WalletException(String code, String field, String value, String message) {
        super(message);
        this.code = code;
        this.field = field;
        this.value = value;
    }

    public static WalletException invalid(String field, String value, String message) {
        return new WalletException("WALLET_INVALID_DATA", field, value, message);
    }

    public static WalletException negativeBalance(String value) {
        return new WalletException("WALLET_NEGATIVE_BALANCE", "balance", value, "Wallet balance cannot be negative");
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
}
