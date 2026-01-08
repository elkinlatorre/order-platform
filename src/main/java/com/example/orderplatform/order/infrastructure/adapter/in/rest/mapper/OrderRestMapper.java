package com.example.orderplatform.order.infrastructure.adapter.in.rest.mapper;

import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.CreateOrderRequest;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.OrderItemResponse;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.OrderResponse;

import java.util.List;

public class OrderRestMapper {

    public static List<OrderItem> toCommand(CreateOrderRequest request) {
        return request.items().stream()
                .map(i -> new OrderItem(
                        i.productId(),
                        i.quantity(),
                        i.price()
                ))
                .toList();
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus().getClass().getSimpleName(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(i -> new OrderItemResponse(
                                i.productId(),
                                i.quantity(),
                                i.price()
                        ))
                        .toList()
        );
    }
}

