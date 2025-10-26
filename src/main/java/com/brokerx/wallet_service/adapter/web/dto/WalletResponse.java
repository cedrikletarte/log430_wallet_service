package com.brokerx.wallet_service.adapter.web.dto;

import java.math.BigDecimal;

public record WalletResponse(
    Long id,
    String currency,
    BigDecimal availableBalance,
    BigDecimal reservedBalance
) {}
