package com.brokerx.wallet_service.adapter.web.dto;

import java.math.BigDecimal;

/* DTO for wallet response */
public record WalletResponse(
    Long id,
    String currency,
    BigDecimal availableBalance,
    BigDecimal reservedBalance
) {}
