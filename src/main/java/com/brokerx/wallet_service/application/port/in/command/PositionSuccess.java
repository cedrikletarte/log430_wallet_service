package com.brokerx.wallet_service.application.port.in.command;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/* Represents a successful position operation */
@Getter
@Builder
@AllArgsConstructor
public class PositionSuccess {

    private Long id;
    private String symbol;
    private int quantity;
    private BigDecimal totalCost;
}
