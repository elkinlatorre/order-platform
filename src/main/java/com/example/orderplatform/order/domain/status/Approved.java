package com.example.orderplatform.order.domain.status;

public record Approved() implements OrderStatus {
    @Override public String name() { return "APPROVED"; }
}
