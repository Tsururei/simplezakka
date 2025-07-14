package com.example.simplezakka.dto.cart;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Map;

import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMaxValidatorForBigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

static @Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto implements Serializable{
    private Integer cartId;
    private Integer userId;
    private Map<Integer, CartItem> cartItems;
    private BIgDecimal totalPrice;
    private Integer totalQuantity;
}
