package com.example.orderplatform.order.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ElementCollection
    @CollectionTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id")
    )
    private List<OrderItemEmbeddable> items;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Instant createdAt;

    protected OrderEntity() {
        // JPA
    }

    public OrderEntity(UUID id,
                       List<OrderItemEmbeddable> items,
                       String status,
                       Instant createdAt) {
        this.id = id;
        this.items = items;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public List<OrderItemEmbeddable> getItems() { return items; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}

