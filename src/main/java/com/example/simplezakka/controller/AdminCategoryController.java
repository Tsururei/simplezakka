package com.example.simplezakka.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.simplezakka.dto.product.CategoryForm;
import com.example.simplezakka.dto.product.CategoryView;
import com.example.simplezakka.service.CategoryService;
import com.example.simplezakka.entity.Category;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryForm form) {
        Category category = categoryService.create(form);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable String categoryId,
            @RequestBody CategoryForm form) {
        Category updated = categoryService.update(categoryId, form);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<CategoryView>> getCategoryList() {
        return ResponseEntity.ok(categoryService.getAll());
    }
}
