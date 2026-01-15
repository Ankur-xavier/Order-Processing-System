package com.peerislands.demo.service.impl;

import com.peerislands.demo.enums.OrderStatus;
import com.peerislands.demo.model.Order;
import com.peerislands.demo.repository.OrderRepository;
import com.peerislands.demo.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repo;

    public OrderServiceImpl(OrderRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        return repo.save(order);
    }

    @Override
    public Optional<Order> getOrder(Long id) {
        return repo.findById(id);
    }

    @Override
    public List<Order> listOrders(Optional<OrderStatus> status) {
        return status.map(repo::findByStatus).orElseGet(repo::findAll);
    }

    @Override
    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order o = repo.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        o.setStatus(newStatus);
        return repo.save(o);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order o = repo.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (o.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled");
        }
        o.setStatus(OrderStatus.CANCELLED);
        return repo.save(o);
    }

    // scheduled method: every 5 minutes convert PENDING -> PROCESSING
    @Override
    @Scheduled(fixedRate = 300_000) // 300000 ms = 5 minutes
    @Transactional
    public void processPendingOrders() {
        List<Order> pending = repo.findByStatus(OrderStatus.PENDING);
        if (pending.isEmpty()) return;
        for (Order o : pending) {
            o.setStatus(OrderStatus.PROCESSING);
            repo.save(o);
        }
    }
}

