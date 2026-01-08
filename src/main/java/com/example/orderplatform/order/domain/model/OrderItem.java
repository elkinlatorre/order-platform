package com.example.orderplatform.order.domain.model;

import java.math.BigDecimal;

public record OrderItem(String productId, int quantity, BigDecimal price) {
    public OrderItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
    }

    public BigDecimal subtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
