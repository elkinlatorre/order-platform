package com.example.orderplatform.order.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(

        @NotEmpty
        List<OrderItemRequest> items
) {
}
