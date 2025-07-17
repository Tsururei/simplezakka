package com.example.simplezakka.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductForm form) {
        Product created = productService.create(form);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ProductAdminView>> getProductList() {
        return ResponseEntity.ok(productService.getAllForAdmin());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody ProductForm form) {
        Product updated = productService.update(id, form);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save")
public ResponseEntity<?> saveProducts(@RequestBody List<ProductForm> products) {
    List<Product> saved = productService.saveAll(products);
    return ResponseEntity.ok(saved);
}
}
