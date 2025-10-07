package com.brokerx.wallet_service.adapter.web;

import com.brokerx.wallet_service.adapter.web.dto.ApiResponse;
import com.brokerx.wallet_service.domain.exception.wallet.WalletException;
import com.brokerx.wallet_service.domain.exception.walletTransaction.WalletTransactionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException by returning a JSON error response.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
            .badRequest()
            .body(new ApiResponse<>(
                "ERROR",
                "INVALID_ARGUMENT",
                ex.getMessage(),
                null
            ));
    }

    /**
     * Handles WalletException by returning a JSON error response.
     */
    @ExceptionHandler(WalletException.class)
    public ResponseEntity<ApiResponse<Void>> handleWalletException(WalletException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(
                "ERROR",
                "WALLET_ERROR",
                ex.getMessage(),
                null
            ));
    }

    /**
     * Handles WalletTransactionException by returning a JSON error response.
     */
    @ExceptionHandler(WalletTransactionException.class)
    public ResponseEntity<ApiResponse<Void>> handleWalletTransactionException(WalletTransactionException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(
                "ERROR",
                "TRANSACTION_ERROR",
                ex.getMessage(),
                null
            ));
    }

    /**
     * Catches any other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        // Log l'erreur pour le debugging
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>(
                "ERROR",
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                null
            ));
    }
}