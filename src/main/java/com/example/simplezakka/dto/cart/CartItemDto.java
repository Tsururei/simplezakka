package com.example.simplezakka.dto.cart;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private String productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer cartQuantity;
    private BigDecimal subTotal;
}
