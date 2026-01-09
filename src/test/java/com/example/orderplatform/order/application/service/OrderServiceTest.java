package com.example.orderplatform.order.application.service;

import com.example.orderplatform.common.exception.OrderCannotBeApprovedException;
import com.example.orderplatform.common.exception.OrderNotFoundException;
import com.example.orderplatform.order.application.port.out.OrderEventPublisherPort;
import com.example.orderplatform.order.application.port.out.OrderRepositoryPort;
import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import com.example.orderplatform.order.domain.status.Approved;
import com.example.orderplatform.order.domain.status.Created;
import com.example.orderplatform.order.domain.status.PendingReview;
import com.example.orderplatform.order.domain.status.Rejected;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private OrderEventPublisherPort eventPublisher;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.of("UTC"));

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, eventPublisher, fixedClock);
    }

    @Test
    void createOrder_ShouldSaveAndPublishEvent() {
        // Given
        List<OrderItem> items = List.of(new OrderItem("p1", 1, new BigDecimal("100.00")));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order result = orderService.createOrder(items);

        // Then
        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(result);
    }

    @Test
    void approveOrder_ShouldApproveAndPublishApprovedEvent() {
        // Given
        UUID orderId = UUID.randomUUID();
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), fixedClock);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order result = orderService.approveOrder(orderId);

        // Then
        assertInstanceOf(Approved.class, result.getStatus());
        verify(eventPublisher).publishOrderApproved(result);
    }

    @Test
    void approveOrder_ShouldSetPendingReviewAndPublishApprovedEvent() {
        // Given
        UUID orderId = UUID.randomUUID();
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("1500.00"))), fixedClock);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order result = orderService.approveOrder(orderId);

        // Then
        assertInstanceOf(PendingReview.class, result.getStatus());
        verify(eventPublisher).publishOrderApproved(result);
    }

    @Test
    void approveOrder_ShouldThrowNotFound_WhenOrderDoesNotExist() {
        // Given
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrderNotFoundException.class, () -> orderService.approveOrder(orderId));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void approveOrder_ShouldThrowCannotBeApproved_WhenStatusIsNotCreated() {
        // Given
        UUID orderId = UUID.randomUUID();
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), fixedClock);
        order.approve(); // Status becomes Approved
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When & Then
        assertThrows(OrderCannotBeApprovedException.class, () -> orderService.approveOrder(orderId));
    }

    @Test
    void approveOrder_ShouldHandleIllegalStateException() {
        // Note: Order.approve() throws IllegalStateException if status is not Created.
        // Although we check this in OrderService, let's ensure the catch block works.
        // We'll use rehydrate to get an order in a state that might cause issues if
        // manipulated.
        UUID orderId = UUID.randomUUID();
        Order order = Order.rehydrate(orderId, List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))),
                Instant.now());
        order.restoreStatus(new Approved());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When & Then
        OrderCannotBeApprovedException exception = assertThrows(OrderCannotBeApprovedException.class,
                () -> orderService.approveOrder(orderId));
        assertTrue(exception.getMessage().contains("Only CREATED orders can be approved"));
    }

    @Test
    void approveOrder_ShouldPublishRejectedEvent_WhenOrderIsRejected() {
        // Note: Currently Order.approve() only sets Approved or PendingReview.
        // To cover the 'instanceof Rejected' branch in OrderService,
        // we'd need a scenario where Order.approve() could result in Rejected,
        // or mock the domain object if possible (though preferred not to mock domain).
        // Looking at Order.java, it doesn't set status to Rejected in approve().
        // However, the Service has: if (saved.getStatus() instanceof Rejected) {
        // publisher.publishOrderRejected(saved); }
        // This suggests a future-proofing or a branch that might be hard to reach with
        // current logic.
        // Let's use reflection or rehydrate to force a Rejected status for coverage of
        // that branch.

        UUID orderId = UUID.randomUUID();
        Order order = Order.rehydrate(orderId, List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))),
                Instant.now());
        // We'll bypass the service logic and mock the save to return a rejected order
        when(orderRepository.findById(orderId)).thenReturn(Optional
                .ofNullable(Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), fixedClock)));

        Order rejectedOrder = Order.rehydrate(orderId, List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))),
                Instant.now());
        rejectedOrder.reject("Manual rejection");

        when(orderRepository.save(any())).thenReturn(rejectedOrder);

        // When
        Order result = orderService.approveOrder(orderId);

        // Then
        assertInstanceOf(Rejected.class, result.getStatus());
        verify(eventPublisher).publishOrderRejected(result);
    }

    @Test
    void getOrderById_ShouldReturnOptional() {
        // Given
        UUID orderId = UUID.randomUUID();
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), fixedClock);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        Optional<Order> result = orderService.getOrderById(orderId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(order, result.get());
    }

    @Test
    void getAllOrders_ShouldReturnList() {
        // Given
        List<Order> orders = List
                .of(Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), fixedClock));
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<Order> result = orderService.getAllOrders();

        // Then
        assertEquals(1, result.size());
        assertEquals(orders, result);
    }
}
