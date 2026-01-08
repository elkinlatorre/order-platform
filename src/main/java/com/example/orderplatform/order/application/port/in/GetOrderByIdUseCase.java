package com.example.orderplatform.order.application.port.in;

import com.example.orderplatform.order.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface GetOrderByIdUseCase {

    Optional<Order> getOrderById(UUID orderId);
}
