package com.example.simplezakka.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderSummaryDto {
    private String orderId;
    private String buyerName;
    private String orderDate;    // ※文字列形式（必要なら LocalDateTime にしても可）
    private BigDecimal totalPrice;
    private String orderStatus;
}
