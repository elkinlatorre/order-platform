package com.example.orderplatform.order.application.port.in;

import com.example.orderplatform.order.domain.model.Order;

import java.util.List;

public interface GetAllOrdersUseCase {

    List<Order> getAllOrders();
}
