package com.example.orderplatform.order.domain.status;

public record PendingReview() implements OrderStatus {
    @Override public String name() { return "PENDING_REVIEW"; }
}
