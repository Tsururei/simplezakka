package com.example.simplezakka.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simplezakka.entity.Category;
import com.example.simplezakka.repository.CategoryRepository;
import com.example.simplezakka.dto.product.CategoryForm;
import com.example.simplezakka.dto.product.CategoryView;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository =categoryRepository;
    }
    public Category create(CategoryForm form) {
        Category category = new Category();
        category.setCategoryId(form.getCategoryId());
        category.setCategoryName(form.getCategoryName());
        return categoryRepository.save(category);
    }

    public Category update(String categoryId, CategoryForm form) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("カテゴリが見つかりません"));
        category.setCategoryName(form.getCategoryName());
        return categoryRepository.save(category);
    }

    public List<CategoryView> getAll() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryView(c.getCategoryId(), c.getCategoryName()))
                .collect(Collectors.toList());
    }

    public void delete(String categoryId) {
    if (categoryRepository.existsById(categoryId)) {
        categoryRepository.deleteById(categoryId);
    } else {
        throw new RuntimeException("削除対象のカテゴリが見つかりません: ID " + categoryId);
    }
    }
}    

