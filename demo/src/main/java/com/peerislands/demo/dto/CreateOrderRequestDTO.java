package com.peerislands.demo.dto;


import java.math.BigDecimal;
import java.util.List;

public class CreateOrderRequestDTO {
    public static class Item {
        public String productId;
        public String name;
        public int quantity;
        public BigDecimal price;
    }
    public List<Item> items;
}
