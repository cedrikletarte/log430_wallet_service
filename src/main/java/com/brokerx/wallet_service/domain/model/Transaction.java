package com.brokerx.wallet_service.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long id;
    private Long walletId;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDate createdAt;
    private LocalDate settledAt;
    private String currency;
    private BigDecimal amount;
    private boolean isSettled;
}
