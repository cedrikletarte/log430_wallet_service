package com.brokerx.wallet_service.adapter.web.dto;

import java.math.BigDecimal;

/**
 * DTO for position response
 */
public record PositionResponse(
    Long userId,
    String symbol,
    String side,
    int quantity,
    BigDecimal price,
    Long orderId
) {}
