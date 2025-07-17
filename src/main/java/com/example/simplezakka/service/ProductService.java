package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductAdminView;
import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import com.example.simplezakka.dto.product.ProductForm;
import com.example.simplezakka.entity.Category;
import com.example.simplezakka.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                           CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
   }
    
    public List<ProductListItem> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }
    
    public ProductDetail findProductById(Integer productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.map(this::convertToDetail).orElse(null);
    }
    
    private ProductListItem convertToListItem(Product product) {
        return new ProductListItem(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
    }
    
private ProductDetail convertToDetail(Product product) {
    ProductDetail productDetail = new ProductDetail();
    productDetail.setProductId(product.getProductId());
    productDetail.setName(product.getName());
    productDetail.setPrice(product.getPrice());
    productDetail.setDescription(product.getDescription());
    productDetail.setStock(product.getStock());
    productDetail.setImageUrl(product.getImageUrl());
    return productDetail;
}

       
    public Product create(ProductForm form) {
        Product product = new Product();
        product.setProductId(form.getProductId());
        product.setName(form.getProductName());
        product.setPrice(form.getProductPrice());
        product.setDescription(form.getDescription());
        product.setStock(form.getStock());
        product.setImageUrl(form.getImageUrl());

        // カテゴリ設定（Optionalでチェック）
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new RuntimeException("カテゴリが見つかりません"));
        product.setCategory(category);

        return productRepository.save(product);
    }

    public List<ProductAdminView> getAllForAdmin() {
        return productRepository.findAll().stream()
            .map(this::convertToAdminView)
            .collect(Collectors.toList());
        };
    
    private ProductAdminView convertToAdminView(Product product) {
    return new ProductAdminView(
        product.getProductId(),
        product.getName(),
        product.getPrice(),
        product.getCategory().getCategoryName(), // カテゴリ名を表示
        product.getImages().size()               // 登録画像数をカウント
    );
}
}
