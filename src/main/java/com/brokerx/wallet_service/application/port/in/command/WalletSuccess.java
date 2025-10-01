package com.brokerx.wallet_service.application.port.in.command;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WalletSuccess {

    private BigDecimal balance;
    private String currency;
}