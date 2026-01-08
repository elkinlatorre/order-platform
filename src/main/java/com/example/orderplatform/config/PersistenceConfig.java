package com.example.orderplatform.config;

import com.example.orderplatform.order.application.port.out.OrderRepositoryPort;
import com.example.orderplatform.order.infrastructure.adapter.out.persistence.JpaOrderRepository;
import com.example.orderplatform.order.infrastructure.adapter.out.persistence.OrderRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {

    @Bean
    OrderRepositoryPort orderRepositoryPort(JpaOrderRepository jpaRepository) {
        return new OrderRepositoryAdapter(jpaRepository);
    }
}
