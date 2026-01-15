package com.peerislands.demo.dto;


import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public class CreateOrderRequestDTO {
    public static class Item {
        @NotBlank public String productId;
        @NotBlank public String name;
        @Min(1) public int quantity;
        @Positive public BigDecimal price;
    }
    @NotEmpty(message = "Order must contain at least one item")
    public List<Item> items;
}
