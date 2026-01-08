package com.example.orderplatform.order.application.service;

import com.example.orderplatform.common.exception.OrderCannotBeApprovedException;
import com.example.orderplatform.common.exception.OrderNotFoundException;
import com.example.orderplatform.order.application.port.in.ApproveOrderUseCase;
import com.example.orderplatform.order.application.port.in.CreateOrderUseCase;
import com.example.orderplatform.order.application.port.in.GetAllOrdersUseCase;
import com.example.orderplatform.order.application.port.in.GetOrderByIdUseCase;
import com.example.orderplatform.order.application.port.out.OrderEventPublisherPort;
import com.example.orderplatform.order.application.port.out.OrderRepositoryPort;
import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import com.example.orderplatform.order.domain.status.Created;
import com.example.orderplatform.order.domain.status.Rejected;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService implements CreateOrderUseCase, ApproveOrderUseCase, GetOrderByIdUseCase, GetAllOrdersUseCase {

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
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!(order.getStatus() instanceof Created)) {
            throw new OrderCannotBeApprovedException(
                    "Only CREATED orders can be approved. Current status: " + order.getStatus().getClass().getSimpleName()
            );
        }

        try {
            order.approve();
        } catch (IllegalStateException e) {
            throw new OrderCannotBeApprovedException(e.getMessage());
        }

        Order saved = orderRepository.save(order);

        if (saved.getStatus() instanceof Rejected) {
            eventPublisher.publishOrderRejected(saved);
        } else {
            eventPublisher.publishOrderApproved(saved);
        }

        return saved;
    }

    @Override
    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}