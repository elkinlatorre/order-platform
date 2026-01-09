package com.example.orderplatform.order.infrastructure.adapter.out.messaging;

import com.example.orderplatform.order.application.port.out.OrderEventPublisherPort;
import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.infrastructure.adapter.out.messaging.event.*;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventPublisher implements OrderEventPublisherPort {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOrderEventPublisher(KafkaTemplate<String, byte[]> kafkaTemplate,
                                    ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishOrderCreated(Order order) {
        send("order.created", order);
    }

    @Override
    public void publishOrderApproved(Order order) {
        send("order.approved", order);
    }

    @Override
    public void publishOrderRejected(Order order) {
        send("order.rejected", order);
    }

    private void send(String topic, Order order) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(order);
            kafkaTemplate.send(topic, order.getId().toString(), payload);
        } catch (Exception e) {
            throw new RuntimeException("Kafka serialization failed", e);
        }
    }
}
