package com.example.orderplatform.order.domain.model;

import com.example.orderplatform.order.domain.status.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final Instant createdAt;

    private Order(UUID id, List<OrderItem> items, Instant createdAt) {
        this.id = id;
        this.items = List.copyOf(items);
        this.status = new Created();
        this.createdAt = createdAt;
    }

    public static Order create(List<OrderItem> items, Clock clock) {
        validateItems(items);
        return new Order(UUID.randomUUID(), items, clock.instant());
    }

    private static void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
    }

    public BigDecimal totalAmount() {
        return items.stream()
                .map(OrderItem::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void approve() {
        if (!(status instanceof Created)) {
            throw new IllegalStateException("Only CREATED orders can be approved");
        }

        if (totalAmount().compareTo(BigDecimal.valueOf(1000)) <= 0) {
            this.status = new Approved();
        } else {
            this.status = new PendingReview();
        }
    }

    public void reject(String reason) {
        if (status instanceof Approved) {
            throw new IllegalStateException("Approved orders cannot be rejected");
        }
        this.status = new Rejected(reason);
    }

    public static Order rehydrate(UUID id,
                                  List<OrderItem> items,
                                  Instant createdAt) {
        Order order = new Order(id, items, createdAt);
        return order;
    }

    public void restoreStatus(OrderStatus status) {
        this.status = status;
    }

    // Getters
    public UUID getId() { return id; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
