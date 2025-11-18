package com.brokerx.wallet_service.infrastructure.kafka.dto;

import java.math.BigDecimal;

/* Event published by order_service when order fails and needs wallet compensation */
public record OrderFailedEvent(
    Long orderId,
    Long walletId,
    String side,
    String stockSymbol,
    Integer quantity,
    BigDecimal limitPrice,
    BigDecimal totalAmount,
    String reason
) {
}
