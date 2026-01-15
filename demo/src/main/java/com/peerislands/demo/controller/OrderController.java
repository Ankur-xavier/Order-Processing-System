package com.peerislands.demo.controller;

import com.peerislands.demo.dto.CreateOrderRequestDTO;
import com.peerislands.demo.dto.OrderResponseDTO;
import com.peerislands.demo.enums.OrderStatus;
import com.peerislands.demo.model.Order;
import com.peerislands.demo.model.OrderItem;
import com.peerislands.demo.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService svc;

    public OrderController(OrderService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody CreateOrderRequestDTO req) {
        Order order = new Order();
        if (req.items != null) {
            order.setItems(req.items.stream()
                .map(i -> new OrderItem(i.productId, i.name, i.quantity, i.price))
                .collect(Collectors.toList()));
        }
        Order saved = svc.createOrder(order);
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> get(@PathVariable Long id) {
        return svc.getOrder(id)
            .map(o -> ResponseEntity.ok(toResponse(o)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<java.util.List<OrderResponseDTO>> list(@RequestParam Optional<OrderStatus> status) {
        var list = svc.listOrders(status).stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(@PathVariable Long id, @RequestBody OrderStatus newStatus) {
        try {
            Order updated = svc.updateStatus(id, newStatus);
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancel(@PathVariable Long id) {
        try {
            Order updated = svc.cancelOrder(id);
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(null); // conflict
        }
    }

    private OrderResponseDTO toResponse(Order o) {
        OrderResponseDTO r = new OrderResponseDTO();
        r.id = o.getId();
        r.items = o.getItems().stream().map(i -> {
            CreateOrderRequestDTO.Item it = new CreateOrderRequestDTO.Item();
            it.productId = i.getProductId();
            it.name = i.getName();
            it.quantity = i.getQuantity();
            it.price = i.getPrice();
            return it;
        }).collect(Collectors.toList());
        r.status = o.getStatus();
        r.createdAt = o.getCreatedAt();
        return r;
    }
}

