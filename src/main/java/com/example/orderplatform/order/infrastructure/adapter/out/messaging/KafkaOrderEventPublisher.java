package com.example.orderplatform.order.infrastructure.adapter.out.messaging;

import com.example.orderplatform.order.application.port.out.OrderEventPublisherPort;
import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.infrastructure.adapter.out.messaging.mapper.OrderEventMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventPublisher implements OrderEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventPublisher.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOrderEventPublisher(KafkaTemplate<String, byte[]> kafkaTemplate,
                                    ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishOrderCreated(Order order) {
        var event = OrderEventMapper.toCreatedEvent(order);
        send("order.created", order.getId().toString(), event);
    }

    @Override
    public void publishOrderApproved(Order order) {
        send("order.approved", order.getId().toString(), order.getId().toString());
    }

    @Override
    public void publishOrderRejected(Order order) {
        send("order.rejected", order.getId().toString(), order.getId().toString());
    }

    private void send(String topic, String key, Object payload) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(payload);

            kafkaTemplate.send(topic, key, bytes)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Kafka send failed → topic={}, key={}", topic, key, ex);
                        } else {
                            log.info("Kafka event sent → topic={}, key={}, offset={}",
                                    topic,
                                    key,
                                    result.getRecordMetadata().offset());
                        }
                    });

        } catch (Exception e) {
            log.error("Kafka serialization failed → topic={}, key={}", topic, key, e);
        }
    }
}