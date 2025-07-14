package com.example.simplezakka.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.cglib.core.Local;

@Entity
@Table(name = "cart_item")
@Data
@NoArgsConstructor
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_itemid")
    private Integer cartItemId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "cart_quantity")
    private Integer cartQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId")
    private Cart cart;
}