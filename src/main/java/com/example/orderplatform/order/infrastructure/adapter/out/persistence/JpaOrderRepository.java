package com.example.orderplatform.order.infrastructure.adapter.out.persistence;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderRepository
        extends JpaRepository<OrderEntity, UUID> {
}
