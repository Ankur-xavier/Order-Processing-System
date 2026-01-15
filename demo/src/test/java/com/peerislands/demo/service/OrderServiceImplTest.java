package com.peerislands.demo.service;

import com.peerislands.demo.enums.OrderStatus;
import com.peerislands.demo.model.Order;
import com.peerislands.demo.model.OrderItem;
import com.peerislands.demo.repository.OrderRepository;
import com.peerislands.demo.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock private OrderRepository repo;
    @InjectMocks private OrderServiceImpl svc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Order samplePending() {
        Order o = new Order();
        o.setId(1L);
        o.setStatus(OrderStatus.PENDING);
        o.setItems(List.of(new OrderItem("p1","name",1, BigDecimal.valueOf(10))));
        return o;
    }

    @Test
    void createOrder_setsPendingAndSaves() {
        Order toSave = new Order();
        when(repo.save(any())).thenAnswer(inv -> {
            Order arg = inv.getArgument(0);
            arg.setId(1L);
            return arg;
        });

        Order created = svc.createOrder(toSave);

        assertEquals(OrderStatus.PENDING, created.getStatus());
        assertNotNull(created.getId());
        verify(repo, times(1)).save(any());
    }

    @Test
    void cancelOrder_onlyWhenPending() {
        Order pending = samplePending();
        when(repo.findById(1L)).thenReturn(Optional.of(pending));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order cancelled = svc.cancelOrder(1L);
        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        verify(repo).save(any());

        // non-pending
        Order shipped = samplePending();
        shipped.setStatus(OrderStatus.SHIPPED);
        when(repo.findById(2L)).thenReturn(Optional.of(shipped));
        Exception ex = assertThrows(IllegalStateException.class, () -> svc.cancelOrder(2L));
        assertTrue(ex.getMessage().contains("Only PENDING"));
    }

    @Test
    void processPendingOrders_updatesAll() {
        Order p1 = samplePending();
        Order p2 = samplePending();
        p2.setId(2L);
        when(repo.findByStatus(OrderStatus.PENDING)).thenReturn(List.of(p1, p2));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        svc.processPendingOrders();

        assertEquals(OrderStatus.PROCESSING, p1.getStatus());
        assertEquals(OrderStatus.PROCESSING, p2.getStatus());
        verify(repo, times(2)).save(any());
    }
}

