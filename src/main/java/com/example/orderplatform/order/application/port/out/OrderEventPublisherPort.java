package com.example.orderplatform.order.application.port.out;

import com.example.orderplatform.order.domain.model.Order;

public interface OrderEventPublisherPort {

    void publishOrderCreated(Order order);

    void publishOrderApproved(Order order);

    void publishOrderRejected(Order order);
}
