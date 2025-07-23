package com.example.simplezakka.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrderResponse {
    private Integer orderId;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private String status;

    private String userName;
    private String userEmail;
    private String shippingAddress;

    private List<OrderItemDto> orderItems;
}
