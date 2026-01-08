package com.example.orderplatform.order.application.port.in;

import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;

import java.util.List;

public interface CreateOrderUseCase {

    Order createOrder(List<OrderItem> items);
}
