package com.example.orderplatform.order.infrastructure.adapter.in.rest;

import com.example.orderplatform.common.exception.OrderNotFoundException;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.CreateOrderRequest;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.dto.OrderResponse;
import com.example.orderplatform.order.infrastructure.adapter.in.rest.mapper.OrderRestMapper;
import com.example.orderplatform.order.application.port.in.ApproveOrderUseCase;
import com.example.orderplatform.order.application.port.in.CreateOrderUseCase;
import com.example.orderplatform.order.application.port.in.GetAllOrdersUseCase;
import com.example.orderplatform.order.application.port.in.GetOrderByIdUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final ApproveOrderUseCase approveOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final GetAllOrdersUseCase getAllOrdersUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                          ApproveOrderUseCase approveOrderUseCase,
                          GetOrderByIdUseCase getOrderByIdUseCase,
                          GetAllOrdersUseCase getAllOrdersUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.approveOrderUseCase = approveOrderUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.getAllOrdersUseCase = getAllOrdersUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody @Valid CreateOrderRequest request) {
        var order = createOrderUseCase.createOrder(
                OrderRestMapper.toCommand(request)
        );
        return OrderRestMapper.toResponse(order);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        return getOrderByIdUseCase.getOrderById(id)
                .map(OrderRestMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return getAllOrdersUseCase.getAllOrders().stream()
                .map(OrderRestMapper::toResponse)
                .toList();
    }

    @PutMapping("/{id}/approve")
    public OrderResponse approveOrder(@PathVariable UUID id) {
        var order = approveOrderUseCase.approveOrder(id);
        return OrderRestMapper.toResponse(order);
    }
}
