package com.brokerx.wallet_service.infrastructure.kafka.dto;

import java.math.BigDecimal;

/* Event received from order_service when an order is executed - contains both buyer and seller info */
public record OrderExecutedEvent(
    Long buyOrderId,
    Long sellOrderId,
    Long buyerWalletId,
    Long sellerWalletId,
    String stockSymbol,
    Integer quantity,
    BigDecimal executionPrice,
    BigDecimal totalAmount
) {}
