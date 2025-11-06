package com.brokerx.wallet_service.infrastructure.kafka.consumer;

import com.brokerx.wallet_service.application.service.WalletCompensationService;
import com.brokerx.wallet_service.infrastructure.kafka.dto.OrderFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka consumer for handling OrderFailed events from order_service
 * Triggers compensation (rollback) by restoring reserved funds
 * Part of Saga Choreography pattern for distributed transaction management
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFailedEventConsumer {

    private final WalletCompensationService compensationService;

    @KafkaListener(
        topics = "${kafka.topic.order-failed:order.failed}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handleOrderFailed(OrderFailedEvent event) {
        log.warn("⚠️ Received OrderFailed event: orderId={}, walletId={}, amount={}, reason={}",
                event.orderId(), event.walletId(), event.totalAmount(), event.reason());

        try {
            // Compensate by restoring funds/shares that were reserved for the order
            compensationService.compensateFailedOrder(
                    event.walletId(),
                    event.orderId(),
                    event.side(),
                    event.stockSymbol(),
                    event.quantity(),
                    event.totalAmount(),
                    event.reason()
            );
            
            log.info("✅ Wallet compensation completed for orderId {} - Funds/shares restored", 
                    event.orderId());
        } catch (Exception e) {
            log.error("❌ Failed to compensate wallet for order {}: {}", 
                    event.orderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to compensate wallet", e);
        }
    }
}
