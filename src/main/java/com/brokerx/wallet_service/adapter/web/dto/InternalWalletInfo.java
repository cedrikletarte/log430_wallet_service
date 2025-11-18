package com.brokerx.wallet_service.adapter.web.dto;

/* Internal representation of wallet information */
public record InternalWalletInfo(
    Long walletId,
    Long userId
) {}
