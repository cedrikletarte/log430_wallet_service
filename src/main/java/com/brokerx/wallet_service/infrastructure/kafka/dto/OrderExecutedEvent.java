package com.brokerx.wallet_service.infrastructure.kafka.dto;

import java.math.BigDecimal;

/**
 * Event received from order_service when an order is executed
 */
public record OrderExecutedEvent(
    Long orderId,
    Long walletId,
    String side,  // BUY or SELL
    String stockSymbol,
    Integer quantity,
    BigDecimal executionPrice,
    BigDecimal totalAmount
) {}
