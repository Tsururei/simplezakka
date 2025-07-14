package com.example.simplezakka.dto.cart;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto implements Serializable{
    private String cart_id;
    private Map<String, CartItem> cartItems;
    private DecimalFormat total_price;
    private Integer total_quantity;
}
