package com.example.orderplatform.order.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void shouldCreateValidOrderItem() {
        // Given
        String productId = "PROD-001";
        int quantity = 5;
        BigDecimal price = new BigDecimal("10.50");

        // When
        OrderItem item = new OrderItem(productId, quantity, price);

        // Then
        assertNotNull(item);
        assertEquals(productId, item.productId());
        assertEquals(quantity, item.quantity());
        assertEquals(price, item.price());
    }

    @Test
    void shouldCalculateSubtotalCorrectly() {
        // Given
        OrderItem item = new OrderItem("PROD-001", 3, new BigDecimal("25.00"));

        // When
        BigDecimal subtotal = item.subtotal();

        // Then
        assertEquals(new BigDecimal("75.00"), subtotal);
    }

    @Test
    void shouldCalculateSubtotalWithDecimals() {
        // Given
        OrderItem item = new OrderItem("PROD-002", 4, new BigDecimal("12.99"));

        // When
        BigDecimal subtotal = item.subtotal();

        // Then
        assertEquals(new BigDecimal("51.96"), subtotal);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem("PROD-001", 0, new BigDecimal("10.00")));
        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem("PROD-001", -5, new BigDecimal("10.00")));
        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem("PROD-001", 5, null));
        assertEquals("Price must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem("PROD-001", 5, BigDecimal.ZERO));
        assertEquals("Price must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem("PROD-001", 5, new BigDecimal("-10.00")));
        assertEquals("Price must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldHandleMinimumValidQuantity() {
        // Given
        OrderItem item = new OrderItem("PROD-001", 1, new BigDecimal("100.00"));

        // When
        BigDecimal subtotal = item.subtotal();

        // Then
        assertEquals(new BigDecimal("100.00"), subtotal);
    }

    @Test
    void shouldHandleMinimumValidPrice() {
        // Given
        OrderItem item = new OrderItem("PROD-001", 10, new BigDecimal("0.01"));

        // When
        BigDecimal subtotal = item.subtotal();

        // Then
        assertEquals(new BigDecimal("0.10"), subtotal);
    }

    @Test
    void shouldHandleLargeQuantities() {
        // Given
        OrderItem item = new OrderItem("PROD-001", 1000, new BigDecimal("5.50"));

        // When
        BigDecimal subtotal = item.subtotal();

        // Then
        assertEquals(new BigDecimal("5500.00"), subtotal);
    }
}
