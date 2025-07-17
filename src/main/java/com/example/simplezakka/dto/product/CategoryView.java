package com.example.simplezakka.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryView {
    private String categoryId;
    private String categoryName;
}
