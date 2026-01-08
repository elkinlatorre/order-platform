package com.example.orderplatform.order.application.port.out;

import com.example.orderplatform.order.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    List<Order> findAll();
}
