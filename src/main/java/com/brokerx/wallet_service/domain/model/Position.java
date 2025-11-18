package com.brokerx.wallet_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/* Represents a stock position within a user's wallet */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position {

    private Long id;
    private Wallet wallet;
    private String symbol;
    private int quantity;
    private BigDecimal totalCost;
}
