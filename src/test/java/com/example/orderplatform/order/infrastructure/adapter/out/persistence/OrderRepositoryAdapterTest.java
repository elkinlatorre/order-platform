package com.example.orderplatform.order.infrastructure.adapter.out.persistence;

import com.example.orderplatform.order.domain.model.Order;
import com.example.orderplatform.order.domain.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterTest {

    @Mock
    private JpaOrderRepository jpaOrderRepository;

    private OrderRepositoryAdapter adapter;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        adapter = new OrderRepositoryAdapter(jpaOrderRepository);
    }

    @Test
    void save_ShouldReturnDomainOrder() {
        // Given
        Order order = Order.create(List.of(new OrderItem("p1", 1, new BigDecimal("100.00"))), fixedClock);
        OrderEntity entity = new OrderEntity(order.getId(), List.of(), "CREATED", order.getCreatedAt());
        when(jpaOrderRepository.save(any(OrderEntity.class))).thenReturn(entity);

        // When
        Order result = adapter.save(order);

        // Then
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        verify(jpaOrderRepository).save(any(OrderEntity.class));
    }

    @Test
    void findById_ShouldReturnDomainOrder() {
        // Given
        UUID id = UUID.randomUUID();
        OrderEntity entity = new OrderEntity(id, List.of(), "CREATED", Instant.now());
        when(jpaOrderRepository.findById(id)).thenReturn(Optional.of(entity));

        // When
        Optional<Order> result = adapter.findById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(jpaOrderRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = adapter.findById(id);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnList() {
        // Given
        OrderEntity entity = new OrderEntity(UUID.randomUUID(), List.of(), "CREATED", Instant.now());
        when(jpaOrderRepository.findAll()).thenReturn(List.of(entity));

        // When
        List<Order> result = adapter.findAll();

        // Then
        assertEquals(1, result.size());
    }
}
