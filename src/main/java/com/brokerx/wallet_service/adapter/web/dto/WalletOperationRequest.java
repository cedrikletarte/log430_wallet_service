package com.brokerx.wallet_service.adapter.web.dto;

import java.math.BigDecimal;

/* DTO for wallet operation request */
public class WalletOperationRequest {
    private BigDecimal amount;
    private String idempotencyKey;

    // Getters et setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}


