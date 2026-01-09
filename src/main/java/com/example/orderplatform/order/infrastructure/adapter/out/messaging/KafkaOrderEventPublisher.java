package com.example.orderplatform.order.infrastructure.adapter.out.messaging;

import com.example.orderplatform.order.application.port.out.OrderEventPublisherPort;
import com.example.orderplatform.order.domain.model.Order;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventPublisher implements OrderEventPublisherPort {

    @Override
    public void publishOrderCreated(Order order) {
        System.out.println("Kafka event: OrderCreated -> " + order.getId());
    }

    @Override
    public void publishOrderApproved(Order order) {
        System.out.println("Kafka event: OrderApproved -> " + order.getId());
    }

    @Override
    public void publishOrderRejected(Order order) {
        System.out.println("Kafka event: OrderRejected -> " + order.getId());
    }
}
