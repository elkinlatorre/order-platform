package com.example.orderplatform.order.infrastructure.adapter.in.rest;

import com.example.orderplatform.common.exception.GlobalExceptionHandler;
import com.example.orderplatform.common.exception.OrderCannotBeApprovedException;
import com.example.orderplatform.order.application.port.in.ApproveOrderUseCase;
import com.example.orderplatform.order.application.port.in.CreateOrderUseCase;
import com.example.orderplatform.order.application.port.in.GetAllOrdersUseCase;
import com.example.orderplatform.order.application.port.in.GetOrderByIdUseCase;
import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private ApproveOrderUseCase approveOrderUseCase;

    @Mock
    private GetOrderByIdUseCase getOrderByIdUseCase;

    @Mock
    private GetAllOrdersUseCase getAllOrdersUseCase;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        OrderController controller = new OrderController(
                createOrderUseCase,
                approveOrderUseCase,
                getOrderByIdUseCase,
                getAllOrdersUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createOrder_ShouldReturn201() throws Exception {
        // Given
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), fixedClock);
        when(createOrderUseCase.createOrder(any())).thenReturn(order);

        String requestBody = """
                {
                    "items": [
                        {
                            "productId": "p1",
                            "quantity": 1,
                            "price": 100.00
                        }
                    ]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("Created"));
    }

    @Test
    void getOrderById_ShouldReturn200() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        Order order = Order.rehydrate(id, List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), Instant.now());
        when(getOrderByIdUseCase.getOrderById(id)).thenReturn(Optional.of(order));

        // When & Then
        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void getOrderById_ShouldReturn404() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(getOrderByIdUseCase.getOrderById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order Not Found"));
    }

    @Test
    void getAllOrders_ShouldReturn200() throws Exception {
        // Given
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), fixedClock);
        when(getAllOrdersUseCase.getAllOrders()).thenReturn(List.of(order));

        // When & Then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void approveOrder_ShouldReturn200() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), fixedClock);
        order.approve();
        when(approveOrderUseCase.approveOrder(id)).thenReturn(order);

        // When & Then
        mockMvc.perform(put("/api/orders/{id}/approve", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Approved"));
    }

    @Test
    void approveOrder_ShouldReturn400_WhenOrderCannotBeApproved() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(approveOrderUseCase.approveOrder(id)).thenThrow(new OrderCannotBeApprovedException("Wait"));

        // When & Then
        mockMvc.perform(put("/api/orders/{id}/approve", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Order Cannot Be Approved"));
    }

    @Test
    void createOrder_ShouldReturn400_WhenIllegalArgumentException() throws Exception {
        // Given
        when(createOrderUseCase.createOrder(any())).thenThrow(new IllegalArgumentException("Invalid data"));
        String requestBody = "{\"items\": [{\"productId\": \"p1\", \"quantity\": 1, \"price\": 100.00}]}";

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Argument"));
    }
}
