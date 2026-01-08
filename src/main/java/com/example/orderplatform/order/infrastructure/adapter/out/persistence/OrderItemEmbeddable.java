package com.example.orderplatform.order.infrastructure.adapter.out.persistence;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class OrderItemEmbeddable {

    private String productId;
    private int quantity;
    private BigDecimal price;

    protected OrderItemEmbeddable() {
        // JPA
    }

    public OrderItemEmbeddable(String productId,
                               int quantity,
                               BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}
