package com.peerislands.demo.repository;

import com.peerislands.demo.enums.OrderStatus;
import com.peerislands.demo.model.Order;  
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
}

