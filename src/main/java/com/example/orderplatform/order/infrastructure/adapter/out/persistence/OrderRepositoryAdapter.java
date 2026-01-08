package com.example.orderplatform.order.infrastructure.adapter.out.persistence;

import com.example.orderplatform.order.application.port.out.OrderRepositoryPort;
import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.infrastructure.mapper.OrderPersistenceMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final JpaOrderRepository jpaRepository;

    public OrderRepositoryAdapter(JpaOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderPersistenceMapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return OrderPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findById(orderId)
                .map(OrderPersistenceMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream()
                .map(OrderPersistenceMapper::toDomain)
                .toList();
    }
}
