package com.example.orderplatform.common.exception;

import com.example.orderplatform.common.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    void handleOrderNotFound_ShouldReturn404() {
        UUID id = UUID.randomUUID();
        OrderNotFoundException ex = new OrderNotFoundException(id);
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOrderNotFound(ex, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertEquals("Order Not Found", response.getBody().error());
        assertEquals(ex.getMessage(), response.getBody().message());
        assertEquals("/api/test", response.getBody().path());
    }

    @Test
    void handleOrderCannotBeApproved_ShouldReturn400() {
        OrderCannotBeApprovedException ex = new OrderCannotBeApprovedException("Error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOrderCannotBeApproved(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals("Order Cannot Be Approved", response.getBody().error());
    }

    @Test
    void handleIllegalArgument_ShouldReturn400() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals("Invalid Argument", response.getBody().error());
    }

    @Test
    @SuppressWarnings("deprecation")
    void handleValidationExceptions_ShouldReturn400() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new FieldError("object", "field", "rejected", false, null, null, "default message"));
        
        MethodParameter parameter = new MethodParameter(this.getClass().getDeclaredMethod("methodForTest", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Failed", response.getBody().get("error"));
        assertNotNull(response.getBody().get("errors"));
        assertEquals("/api/test", response.getBody().get("path"));
    }

    public void methodForTest(String arg) {}
}
