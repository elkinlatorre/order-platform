package com.example.orderplatform.order.domain.status;

public record Created() implements OrderStatus {
    @Override public String name() { return "CREATED"; }
}
