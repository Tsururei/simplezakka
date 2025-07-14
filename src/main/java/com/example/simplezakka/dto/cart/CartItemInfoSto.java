package com.example.simplezakka.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemInfoSto {
    private String productId;
    private String quantity;
    
}
