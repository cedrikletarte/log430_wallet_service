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
        log.info("📥 Received OrderExecuted event: orderId={}, walletId={}, side={}, symbol={}, qty={} @ {}, total={}",
                event.orderId(), event.walletId(), event.side(), event.stockSymbol(),
                event.quantity(), event.executionPrice(), event.totalAmount());

        try {
            // Settle the matched order with full details including position updates
            orderWalletUseCase.settleMatchedOrder(
                    event.walletId(),
                    event.side(),
                    event.stockSymbol(),
                    event.quantity(),
                    event.executionPrice(),
                    event.totalAmount(),
                    event.orderId()
            );
            
            log.info("✅ Order settlement completed for orderId {} ({})", 
                    event.orderId(), event.side());
            
            // Publish WalletSettled event for Saga Choreography
            // This ensures wallet is updated BEFORE notification is sent
            WalletSettledEvent settledEvent = new WalletSettledEvent(
                    event.orderId(),
                    event.walletId(),
                    event.side(),
                    event.stockSymbol(),
                    event.quantity(),
                    event.executionPrice(),
                    event.totalAmount()
            );
            
            kafkaTemplate.send(walletSettledTopic, event.stockSymbol(), settledEvent);
            log.info("📤 Published WalletSettled event for orderId {} to topic {}", 
                    event.orderId(), walletSettledTopic);
            
        } catch (Exception e) {
            log.error("❌ Failed to process OrderExecuted event for orderId {}: {}",
                    event.orderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to process OrderExecuted event", e);
        }
    }
}
