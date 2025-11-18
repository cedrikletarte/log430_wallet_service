package com.brokerx.wallet_service.infrastructure.kafka.dto;

import java.math.BigDecimal;

/* Event published when an order has been settled in the wallet */
public record WalletSettledEvent(
    Long orderId,
    Long walletId,
    String side,
    String stockSymbol,
    Integer quantity,
    BigDecimal executionPrice,
    BigDecimal totalAmount
) {}
