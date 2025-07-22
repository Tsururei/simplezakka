package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductAdminView;
import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.dto.product.ProductForm;
import com.example.simplezakka.entity.Category;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.CategoryRepository;
import com.example.simplezakka.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private Product productWithNullFields;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setCategoryId("1"); 
        category.setCategoryName("テストカテゴリ");

        product1 = new Product();
        product1.setProductId(1);
        product1.setName("商品1");
        product1.setPrice(100);
        product1.setImageUrl("/img1.png");
        product1.setDescription("説明1");
        product1.setStock(10);
        product1.setCategory(category);
        
        product2 = new Product();
        product2.setProductId(2);
        product2.setName("商品2");
        product2.setPrice(200);
        product2.setImageUrl("/img2.png");
        product2.setDescription("説明2");
        product2.setStock(5);
        product2.setCategory(category);

        productWithNullFields = new Product();
        productWithNullFields.setProductId(3);
        productWithNullFields.setName("商品3（Nullあり）");
        productWithNullFields.setPrice(300);
        productWithNullFields.setStock(8);
        productWithNullFields.setDescription(null);
        productWithNullFields.setImageUrl(null);
        productWithNullFields.setCategory(category);
 
     
    }

    // === findAllProducts ===

    @Test
    void findAllProducts_ShouldReturnListOfProductListItems() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<ProductListItem> result = productService.findAllProducts();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("productId", "name", "price", "imageUrl")
                .containsExactlyInAnyOrder(
                        tuple(1, "商品1", 100, "/img1.png"),
                        tuple(2, "商品2", 200, "/img2.png")
                );
        verify(productRepository).findAll();
    }

    @Test
    void findAllProducts_WhenRepositoryReturnsEmptyList_ShouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductListItem> result = productService.findAllProducts();

        assertThat(result).isEmpty();
        verify(productRepository).findAll();
    }

    @Test
    void findAllProducts_WhenProductHasNullFields_ShouldMapNullToDto() {
        when(productRepository.findAll()).thenReturn(List.of(productWithNullFields));

        List<ProductListItem> result = productService.findAllProducts();

        assertThat(result).hasSize(1);
        ProductListItem dto = result.get(0);
        assertThat(dto.getImageUrl()).isNull();
    }

    // === findProductById ===

    @Test
    void findProductById_WhenProductExists_ShouldReturnProductDetail() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));

        ProductDetail result = productService.findProductById(1);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(1);
    }

    @Test
    void findProductById_WhenProductNotExists_ShouldReturnNull() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        ProductDetail result = productService.findProductById(99);

        assertThat(result).isNull();
    }

    @Test
    void findProductById_WhenProductHasNullFields_ShouldMapNullToDto() {
        when(productRepository.findById(3)).thenReturn(Optional.of(productWithNullFields));

        ProductDetail result = productService.findProductById(3);

        assertThat(result.getDescription()).isNull();
        assertThat(result.getImageUrl()).isNull();
    }

    @Test
    void findProductById_WhenProductIdIsNull_ShouldReturnNull() {
        when(productRepository.findById(null)).thenReturn(Optional.empty());

        ProductDetail result = productService.findProductById(null);

        assertThat(result).isNull();
    }

    // === create ===

    @Test
    void getAllForAdmin_WhenNoProducts_ShouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductAdminView> result = productService.getAllForAdmin();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllForAdmin_WhenProductHasNoImages_ShouldReturnZeroImageCount() {
        Product product = new Product();
        product.setProductId(1);
        product.setName("商品B");
        product.setPrice(555);
        Category category = new Category();
        category.setCategoryName("雑貨");
        product.setCategory(category);
        product.setImages(Collections.emptyList());

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductAdminView> result = productService.getAllForAdmin();

        assertThat(result.get(0).getImageCount()).isEqualTo(0);
    }
}
