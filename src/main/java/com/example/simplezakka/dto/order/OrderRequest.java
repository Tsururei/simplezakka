package com.example.simplezakka.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @Valid
    @NotNull(message = "顧客情報は必須です")
    private CustomerInfo customerInfo;

    @NotEmpty(message = "注文商品は必須です")
    private List<OrderItemDto> items;

}