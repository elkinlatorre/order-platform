package com.example.orderplatform.order.application.port.in;

import com.example.orderplatform.order.domain.model.Order;

import java.util.UUID;

public interface ApproveOrderUseCase {

    Order approveOrder(UUID orderId);
}
