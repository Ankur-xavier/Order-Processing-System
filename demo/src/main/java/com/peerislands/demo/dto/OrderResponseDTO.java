package com.peerislands.demo.dto;


import java.time.Instant;
import java.util.List;

import com.peerislands.demo.enums.OrderStatus;

public class OrderResponseDTO {
    public Long id;
    public List<CreateOrderRequestDTO.Item> items;
    public OrderStatus status;
    public Instant createdAt;
}
