package com.example.simplezakka.dto.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductAdminView {
    private Integer productId;
    private String productName;
    private Integer productPrice;
    private String categoryId;
    private String categoryName;
    private int imageCount;
    private Integer stock;
    private String imageUrl;
}
