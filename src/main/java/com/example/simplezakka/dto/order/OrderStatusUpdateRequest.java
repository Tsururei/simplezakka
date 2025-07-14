package com.example.simplezakka.dto.order;

import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    private String newStatus;
}
