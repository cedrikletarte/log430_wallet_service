package com.brokerx.wallet_service.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long id;
    private Wallet wallet;
    private Long orderId;
    private TransactionType type;
    private TransactionStatus status;
    private Instant createdAt;
    private Instant settledAt;
    private BigDecimal amount;
    private boolean isSettled;
}
