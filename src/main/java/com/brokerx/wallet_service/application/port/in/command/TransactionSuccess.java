package com.brokerx.wallet_service.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.model.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TransactionSuccess {
    
    private Long id;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDate createdAt;
    private LocalDate settledAt;
    private String currency;
    private BigDecimal amount;
    private boolean isSettled;
}