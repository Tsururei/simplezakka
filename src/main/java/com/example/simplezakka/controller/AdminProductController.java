package com.example.simplezakka.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.simplezakka.dto.product.ProductAdminView;
import com.example.simplezakka.dto.product.ProductForm;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    
    private final ProductService productService;

    @PostMapping("/admin/products")
    public ResponseEntity<?> createProduct(@RequestBody ProductForm form) {
        Product created = productService.create(form);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ProductAdminView>> getProductList() {
        return ResponseEntity.ok(productService.getAllForAdmin());
    }

    // updateProduct, deleteProduct も同様
}
