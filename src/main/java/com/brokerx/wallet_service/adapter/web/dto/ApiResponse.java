package com.brokerx.wallet_service.adapter.web.dto;

/* Standard API response wrapper */
public record ApiResponse<T>(
    String status,
    String errorCode,
    String message,
    T data
) {}

