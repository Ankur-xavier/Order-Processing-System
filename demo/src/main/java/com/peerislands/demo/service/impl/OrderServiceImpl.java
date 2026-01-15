package com.peerislands.demo.service.impl;

import com.peerislands.demo.enums.OrderStatus;
import com.peerislands.demo.exception.InvalidOrderStateException;
import com.peerislands.demo.exception.OrderNotFoundException;
import com.peerislands.demo.model.Order;
import com.peerislands.demo.repository.OrderRepository;
import com.peerislands.demo.service.OrderService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repo;

    public OrderServiceImpl(OrderRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        log.info("Creating order with {} items", order.getItems().size());
        order.setStatus(OrderStatus.PENDING);
        Order saved = repo.save(order);
        log.info("Order created. orderId={}, status={}", saved.getId(), saved.getStatus());
        return saved;
    }

    @Override
    public Optional<Order> getOrder(Long id) {
        log.debug("Fetching order. orderId={}", id);
         if (repo.existsById(id)) {
            return repo.findById(id);
        } else {
            log.warn("Order not found. orderId={}", id);
            throw new OrderNotFoundException(id);
        }
    }

    @Override
    public List<Order> listOrders(Optional<OrderStatus> status) {
        log.debug("Listing orders. status={}", status.orElse(null));
        return status.map(repo::findByStatus).orElseGet(repo::findAll);
    }

    @Override
    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        log.debug("Updating order status. orderId={}, newStatus={}", orderId, newStatus);
        Order order = repo.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        log.info("Order status updated. orderId={}, {} -> {}", orderId, oldStatus, newStatus);
        return repo.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        log.debug("Cancelling order. orderId={}", orderId);
        Order order = repo.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
            "Only PENDING orders can be cancelled. current=" + order.getStatus()
        );
    }
        order.setStatus(OrderStatus.CANCELLED);
        log.info("Order cancelled. orderId={}", orderId);
        return repo.save(order);
    }

    @Override           
    @Transactional
    public void processPendingOrders() {
        log.debug("Order scheduler triggered");
        List<Order> pending = repo.findByStatus(OrderStatus.PENDING);
        if (pending.isEmpty()){
            log.debug("No PENDING orders found");
            return;
        }
        log.info("Processing {} pending orders", pending.size());
        for (Order o : pending) {
            o.setStatus(OrderStatus.PROCESSING);
            repo.save(o);
            log.info("Order moved to PROCESSING. orderId={}", o.getId());
        }
    }
}

