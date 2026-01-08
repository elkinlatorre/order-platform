package com.example.orderplatform.order.domain.status;

public sealed interface OrderStatus
        permits Created, Approved, Rejected, PendingReview {
    String name();
}
