package com.example.orderplatform.common.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderServiceExceptionTest {

    @Test
    void orderNotFoundException_ShouldHaveCorrectMessage() {
        UUID id = UUID.randomUUID();
        OrderNotFoundException exception = new OrderNotFoundException(id);
        assertEquals("Order with id " + id + " not found", exception.getMessage());
    }

    @Test
    void orderCannotBeApprovedException_ShouldHaveCorrectMessage() {
        String message = "Custom error message";
        OrderCannotBeApprovedException exception = new OrderCannotBeApprovedException(message);
        assertEquals(message, exception.getMessage());
    }
}
