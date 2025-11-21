package com.brokerx.wallet_service.infrastructure.kafka.consumer;

import com.brokerx.wallet_service.application.port.in.useCase.OrderWalletUseCase;
import com.brokerx.wallet_service.infrastructure.kafka.dto.OrderExecutedEvent;
import com.brokerx.wallet_service.infrastructure.kafka.dto.WalletSettledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/* Kafka consumer for handling OrderExecuted events from order_service */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExecutedEventConsumer {

    private final OrderWalletUseCase orderWalletUseCase;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topic.wallet-settled:wallet.settled}")
    private String walletSettledTopic;

    /* Listens for OrderExecuted events and processes order settlement */
    @KafkaListener(
        topics = "${kafka.topic.order-executed}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handleOrderExecuted(OrderExecutedEvent event) {
        log.info("Received OrderExecuted event: buyOrderId={}, sellOrderId={}, buyerWallet={}, sellerWallet={}, symbol={}, qty={} @ {}",
                event.buyOrderId(), event.sellOrderId(), event.buyerWalletId(), event.sellerWalletId(),
                event.stockSymbol(), event.quantity(), event.executionPrice());

        try {
            // Settle buyer's order (debit cash, credit position)
            orderWalletUseCase.settleMatchedOrder(
                    event.buyerWalletId(),
                    "BUY",
                    event.stockSymbol(),
                    event.quantity(),
                    event.executionPrice(),
                    event.totalAmount(),
                    event.buyOrderId()
            );
            
            log.info("Buyer wallet settlement completed for orderId {} (wallet={})", 
                    event.buyOrderId(), event.buyerWalletId());

            // Settle seller's order (credit cash, debit position)
            orderWalletUseCase.settleMatchedOrder(
                    event.sellerWalletId(),
                    "SELL",
                    event.stockSymbol(),
                    event.quantity(),
                    event.executionPrice(),
                    event.totalAmount(),
                    event.sellOrderId()
            );
            
            log.info("Seller wallet settlement completed for orderId {} (wallet={})", 
                    event.sellOrderId(), event.sellerWalletId());
            
            // Publish WalletSettled events for both orders
            WalletSettledEvent buyerSettledEvent = new WalletSettledEvent(
                    event.buyOrderId(),
                    event.buyerWalletId(),
                    "BUY",
                    event.stockSymbol(),
                    event.quantity(),
                    event.executionPrice(),
                    event.totalAmount()
            );
            
            WalletSettledEvent sellerSettledEvent = new WalletSettledEvent(
                    event.sellOrderId(),
                    event.sellerWalletId(),
                    "SELL",
                    event.stockSymbol(),
                    event.quantity(),
                    event.executionPrice(),
                    event.totalAmount()
            );
            
            kafkaTemplate.send(walletSettledTopic, event.stockSymbol(), buyerSettledEvent);
            kafkaTemplate.send(walletSettledTopic, event.stockSymbol(), sellerSettledEvent);
            
            log.info("Published WalletSettled events for orders {} and {} to topic {}", 
                    event.buyOrderId(), event.sellOrderId(), walletSettledTopic);
            
        } catch (Exception e) {
            log.error("Failed to process OrderExecuted event for orders {} and {}: {}",
                    event.buyOrderId(), event.sellOrderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to process OrderExecuted event", e);
        }
    }
}
