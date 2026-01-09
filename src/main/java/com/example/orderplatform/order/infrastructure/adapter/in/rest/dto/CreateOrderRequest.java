package com.example.orderplatform.order.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(

        @NotEmpty
        List<OrderItemRequest> items
) {
}
