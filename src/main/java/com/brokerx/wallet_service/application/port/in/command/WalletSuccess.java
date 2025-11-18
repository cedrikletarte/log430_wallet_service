package com.brokerx.wallet_service.application.port.in.command;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/* Represents a successful wallet operation */
@Getter
@Builder
@AllArgsConstructor
public class WalletSuccess {

    private Long id;
    private BigDecimal availableBalance;
    private BigDecimal reservedBalance;
    private String currency;
}