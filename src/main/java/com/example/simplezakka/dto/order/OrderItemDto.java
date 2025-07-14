package com.example.simplezakka.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemDto {
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;  // unitPrice × quantity
}
