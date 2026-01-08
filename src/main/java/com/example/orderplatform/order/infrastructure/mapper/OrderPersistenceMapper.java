package com.example.orderplatform.order.infrastructure.mapper;

import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import com.example.orderplatform.order.domain.status.*;
import com.example.orderplatform.order.infrastructure.adapter.out.persistence.OrderEntity;
import com.example.orderplatform.order.infrastructure.adapter.out.persistence.OrderItemEmbeddable;

import java.util.List;
import java.util.stream.Collectors;

public final class OrderPersistenceMapper {

    private OrderPersistenceMapper() {}

    public static OrderEntity toEntity(Order order) {
        return new OrderEntity(
                order.getId(),
                toEmbeddables(order.getItems()),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }

    public static Order toDomain(OrderEntity entity) {
        Order order = Order.rehydrate(
                entity.getId(),
                toDomainItems(entity.getItems()),
                entity.getCreatedAt()
        );
        order.restoreStatus(fromStatus(entity.getStatus()));
        return order;
    }

    private static List<OrderItemEmbeddable> toEmbeddables(List<OrderItem> items) {
        return items.stream()
                .map(i -> new OrderItemEmbeddable(
                        i.productId(),
                        i.quantity(),
                        i.price()
                ))
                .collect(Collectors.toList());
    }

    private static List<OrderItem> toDomainItems(List<OrderItemEmbeddable> items) {
        return items.stream()
                .map(i -> new OrderItem(
                        i.getProductId(),
                        i.getQuantity(),
                        i.getPrice()
                ))
                .collect(Collectors.toList());
    }

    private static OrderStatus fromStatus(String status) {
        return switch (status) {
            case "CREATED" -> new Created();
            case "APPROVED" -> new Approved();
            case "PENDING_REVIEW" -> new PendingReview();
            case "REJECTED" -> new Rejected("Restored from persistence");
            default -> throw new IllegalStateException("Unknown status: " + status);
        };
    }
}
