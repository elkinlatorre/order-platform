package com.example.orderplatform.order.infrastructure.adapter.out.messaging.mapper;

import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.infrastructure.adapter.out.messaging.event.*;

import java.util.stream.Collectors;

public final class OrderEventMapper {

    private OrderEventMapper() {}

    public static OrderCreatedEvent toCreatedEvent(Order order) {
        return new OrderCreatedEvent(
                order.getId().toString(),
                order.getCreatedAt(),
                order.totalAmount(),
                order.getItems().stream()
                        .map(i -> new OrderItemEvent(
                                i.productId(),
                                i.quantity(),
                                i.price()
                        ))
                        .collect(Collectors.toList())
        );
    }
}
