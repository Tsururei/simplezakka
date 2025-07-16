package com.example.simplezakka.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Category {

    @Id
    private String categoryId;

    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
