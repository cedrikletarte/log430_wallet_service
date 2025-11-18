package com.brokerx.wallet_service.application.port.in.useCase;

import java.math.BigDecimal;

public interface OrderWalletUseCase {
    
    /* Reserve funds in wallet for an order (using walletId directly) */
    void reserveFundsForWallet(Long walletId, BigDecimal amount, Long orderId);
    
    /* Refund a cancelled order by releasing reserved funds */
    void refundCancelledOrder(Long walletId, BigDecimal amount, Long orderId);
    
    /* Execute an order transaction (for completed orders) */
    void executeOrder(Long userId, String stockSymbol, String side, Integer quantity, BigDecimal price, Long orderId);
    
    /* Settle a matched order transaction (using walletId directly from matching service) */
    void settleMatchedOrder(Long walletId, String side, String stockSymbol, 
                           Integer quantity, BigDecimal executionPrice, 
                           BigDecimal totalAmount, Long orderId);
}
