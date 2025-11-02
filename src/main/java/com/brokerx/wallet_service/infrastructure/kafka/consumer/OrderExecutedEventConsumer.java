package com.brokerx.wallet_service.infrastructure.kafka.consumer;

import com.brokerx.wallet_service.application.port.in.useCase.OrderWalletUseCase;
import com.brokerx.wallet_service.infrastructure.kafka.dto.OrderExecutedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka consumer for handling OrderExecuted events from order_service
 * This is an inbound adapter in hexagonal architecture
 * Handles final settlement of matched orders
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExecutedEventConsumer {

    private final OrderWalletUseCase orderWalletUseCase;

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
        } catch (Exception e) {
            log.error("❌ Failed to process OrderExecuted event for orderId {}: {}",
                    event.orderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to process OrderExecuted event", e);
        }
    }
}
