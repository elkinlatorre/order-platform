package com.example.orderplatform.order.domain.status;

public record Rejected(String reason) implements OrderStatus {
    @Override public String name() { return "REJECTED"; }
}
