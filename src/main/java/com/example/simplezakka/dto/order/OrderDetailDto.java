package com.example.simplezakka.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderDetailDto {
    private String orderId;
    private String buyerName;
    private String shippingAddress;
    private List<OrderItemDto> items;
    private BigDecimal totalPrice;
    private String orderStatus;
}
