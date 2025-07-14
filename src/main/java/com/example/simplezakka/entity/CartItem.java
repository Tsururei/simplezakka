package com.example.simplezakka.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.cglib.core.Local;

 @Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
public class CartItem {

    @Column(name = "cart_id")
    private Integer cartId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "cart_quantity")
    private Integer cartQuantity;
}