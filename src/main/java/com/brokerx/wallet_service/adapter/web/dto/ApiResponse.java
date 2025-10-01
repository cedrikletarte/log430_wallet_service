package com.brokerx.wallet_service.adapter.web.dto;

public record ApiResponse<T>(
    String status,
    String errorCode,
    String message,
    T data
) {}

