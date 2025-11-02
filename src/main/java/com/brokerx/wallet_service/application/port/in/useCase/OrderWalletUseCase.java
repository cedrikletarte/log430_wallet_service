package com.brokerx.wallet_service.application.port.in.useCase;

import java.math.BigDecimal;

/**
 * Use case for handling order-related wallet operations
 * This follows hexagonal architecture by defining the input port in the application layer
 */
public interface OrderWalletUseCase {
    
    /**
     * Reserve funds in wallet for an order (using walletId directly)
     * @param walletId The wallet ID
     * @param amount The amount to reserve
     */
    void reserveFundsForWallet(Long walletId, BigDecimal amount);
    
    /**
     * Refund a cancelled order by releasing reserved funds
     * @param walletId The wallet ID
     * @param amount The amount to refund
     * @param orderId The order ID for tracking
     */
    void refundCancelledOrder(Long walletId, BigDecimal amount, Long orderId);
    
    /**
     * Execute an order transaction (for completed orders)
     * @param userId The user ID
     * @param stockSymbol The stock symbol
     * @param side The order side (BUY/SELL)
     * @param quantity The quantity
     * @param price The execution price
     * @param orderId The order ID
     */
    void executeOrder(Long userId, String stockSymbol, String side, Integer quantity, BigDecimal price, Long orderId);
    
    /**
     * Settle a matched order transaction (using walletId directly from matching service)
     * Updates both wallet balance and stock positions
     * 
     * @param walletId The wallet ID
     * @param side The order side (BUY/SELL)
     * @param stockSymbol The stock symbol
     * @param quantity The quantity traded
     * @param executionPrice The execution price per share
     * @param totalAmount The total amount (quantity * executionPrice)
     * @param orderId The order ID for tracking
     */
    void settleMatchedOrder(Long walletId, String side, String stockSymbol, 
                           Integer quantity, BigDecimal executionPrice, 
                           BigDecimal totalAmount, Long orderId);
}
