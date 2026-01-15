package com.peerislands.demo.service;

import com.peerislands.demo.enums.OrderStatus;
import com.peerislands.demo.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    Optional<Order> getOrder(Long id);
    List<Order> listOrders(Optional<OrderStatus> status);
    Order updateStatus(Long orderId, OrderStatus newStatus);
    Order cancelOrder(Long orderId);
    void processPendingOrders(); // scheduled task
}
