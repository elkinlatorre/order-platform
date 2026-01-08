package com.example.orderplatform.common.exception;

public class OrderCannotBeApprovedException extends RuntimeException {
    
    public OrderCannotBeApprovedException(String message) {
        super(message);
    }
}
