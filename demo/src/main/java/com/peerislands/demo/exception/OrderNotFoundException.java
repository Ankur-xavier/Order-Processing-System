package com.peerislands.demo.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Order not found. id=" + orderId);
    }
}
