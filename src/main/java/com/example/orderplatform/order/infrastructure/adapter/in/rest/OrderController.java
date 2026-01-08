package com.example.orderplatform.order.infrastructure.adapter.in.rest;

import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.CreateOrderRequest;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.OrderResponse;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.mapper.OrderRestMapper;
import com.example.orderplatform.order.application.port.in.CreateOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody @Valid CreateOrderRequest request) {
        var order = createOrderUseCase.createOrder(
                OrderRestMapper.toCommand(request)
        );

        return OrderRestMapper.toResponse(order);
    }
}
