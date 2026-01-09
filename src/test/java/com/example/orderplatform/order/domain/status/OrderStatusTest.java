package com.example.orderplatform.order.domain.status;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void createdStatusShouldReturnCorrectName() {
        // Given
        OrderStatus status = new Created();

        // When
        String name = status.name();

        // Then
        assertEquals("CREATED", name);
    }

    @Test
    void approvedStatusShouldReturnCorrectName() {
        // Given
        OrderStatus status = new Approved();

        // When
        String name = status.name();

        // Then
        assertEquals("APPROVED", name);
    }

    @Test
    void rejectedStatusShouldReturnCorrectName() {
        // Given
        String reason = "Insufficient inventory";
        OrderStatus status = new Rejected(reason);

        // When
        String name = status.name();

        // Then
        assertEquals("REJECTED", name);
    }

    @Test
    void rejectedStatusShouldStoreReason() {
        // Given
        String reason = "Payment failed";
        Rejected status = new Rejected(reason);

        // When
        String storedReason = status.reason();

        // Then
        assertEquals(reason, storedReason);
    }

    @Test
    void pendingReviewStatusShouldReturnCorrectName() {
        // Given
        OrderStatus status = new PendingReview();

        // When
        String name = status.name();

        // Then
        assertEquals("PENDING_REVIEW", name);
    }

    @Test
    void createdStatusShouldBeInstanceOfOrderStatus() {
        // Given
        Created status = new Created();

        // Then
        assertInstanceOf(OrderStatus.class, status);
    }

    @Test
    void approvedStatusShouldBeInstanceOfOrderStatus() {
        // Given
        Approved status = new Approved();

        // Then
        assertInstanceOf(OrderStatus.class, status);
    }

    @Test
    void rejectedStatusShouldBeInstanceOfOrderStatus() {
        // Given
        Rejected status = new Rejected("Test reason");

        // Then
        assertInstanceOf(OrderStatus.class, status);
    }

    @Test
    void pendingReviewStatusShouldBeInstanceOfOrderStatus() {
        // Given
        PendingReview status = new PendingReview();

        // Then
        assertInstanceOf(OrderStatus.class, status);
    }

    @Test
    void rejectedStatusWithDifferentReasonsShouldNotBeEqual() {
        // Given
        Rejected status1 = new Rejected("Reason 1");
        Rejected status2 = new Rejected("Reason 2");

        // Then
        assertNotEquals(status1, status2);
    }

    @Test
    void rejectedStatusWithSameReasonShouldBeEqual() {
        // Given
        String reason = "Same reason";
        Rejected status1 = new Rejected(reason);
        Rejected status2 = new Rejected(reason);

        // Then
        assertEquals(status1, status2);
    }

    @Test
    void createdStatusInstancesShouldBeEqual() {
        // Given
        Created status1 = new Created();
        Created status2 = new Created();

        // Then
        assertEquals(status1, status2);
    }

    @Test
    void approvedStatusInstancesShouldBeEqual() {
        // Given
        Approved status1 = new Approved();
        Approved status2 = new Approved();

        // Then
        assertEquals(status1, status2);
    }

    @Test
    void pendingReviewStatusInstancesShouldBeEqual() {
        // Given
        PendingReview status1 = new PendingReview();
        PendingReview status2 = new PendingReview();

        // Then
        assertEquals(status1, status2);
    }
}
