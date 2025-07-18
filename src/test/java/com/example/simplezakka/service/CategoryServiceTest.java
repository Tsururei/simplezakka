package com.example.simplezakka.service;

import com.example.simplezakka.entity.Category;
import com.example.simplezakka.repository.CategoryRepository;
import com.example.simplezakka.dto.product.CategoryForm;
import com.example.simplezakka.dto.product.CategoryView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCategory() {
        CategoryForm form = new CategoryForm();
        form.setCategoryName("キッチン");

        Category savedCategory = new Category();
        savedCategory.setCategoryId("random-uuid");
        savedCategory.setCategoryName("キッチン");

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.create(form);

        assertNotNull(result);
        assertEquals("キッチン", result.getCategoryName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_Success() {
        Category existingCategory = new Category();
        existingCategory.setCategoryId("id-123");
        existingCategory.setCategoryName("旧カテゴリ");

        CategoryForm form = new CategoryForm();
        form.setCategoryName("新カテゴリ");

        when(categoryRepository.findById("id-123")).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        Category result = categoryService.update("id-123", form);

        assertEquals("新カテゴリ", result.getCategoryName());
        verify(categoryRepository).findById("id-123");
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void testUpdateCategory_NotFound() {
        CategoryForm form = new CategoryForm();
        form.setCategoryName("新カテゴリ");

        when(categoryRepository.findById("id-999")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            categoryService.update("id-999", form);
        });
        assertEquals("カテゴリが見つかりません", ex.getMessage());
    }

    @Test
    void testGetAllCategories() {
        Category category1 = new Category();
        category1.setCategoryId("id-1");
        category1.setCategoryName("キッチン");

        Category category2 = new Category();
        category2.setCategoryId("id-2");
        category2.setCategoryName("インテリア");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        List<CategoryView> views = categoryService.getAll();

        assertEquals(2, views.size());
        assertEquals("キッチン", views.get(0).getCategoryName());
        assertEquals("インテリア", views.get(1).getCategoryName());
        verify(categoryRepository).findAll();
    }
}
