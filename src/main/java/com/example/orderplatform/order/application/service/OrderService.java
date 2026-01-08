package com.example.orderplatform.order.application.service;

import com.example.orderplatform.order.application.port.in.ApproveOrderUseCase;
import com.example.orderplatform.order.application.port.in.CreateOrderUseCase;
import com.example.orderplatform.order.application.port.out.OrderEventPublisherPort;
import com.example.orderplatform.order.application.port.out.OrderRepositoryPort;
import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import com.example.orderplatform.order.domain.status.Rejected;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService implements CreateOrderUseCase, ApproveOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;
    private final Clock clock;

    public OrderService(OrderRepositoryPort orderRepository,
                        OrderEventPublisherPort eventPublisher,
                        Clock clock) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    public Order createOrder(List<OrderItem> items) {
        Order order = Order.create(items, clock);
        Order saved = orderRepository.save(order);

        eventPublisher.publishOrderCreated(saved);
        return saved;
    }

    @Override
    public Order approveOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.approve();
        Order saved = orderRepository.save(order);

        if (saved.getStatus() instanceof Rejected) {
            eventPublisher.publishOrderRejected(saved);
        } else {
            eventPublisher.publishOrderApproved(saved);
        }

        return saved;
    }
}