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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.ArrayList;
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
                product.getImageUrl(),
                product.getCategory().getCategoryId()
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
        product.setName(form.getProductName());
        product.setPrice(form.getProductPrice());
        product.setDescription(form.getDescription());
        product.setStock(form.getStock());
        product.setImageUrl(form.getImageUrl());

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
        product.getCategory().getCategoryId(),
        product.getCategory().getCategoryName(), 
        product.getImages().size(),   
        product.getStock()            
    );
}

    public void delete(Integer id) {
       if (productRepository.existsById(id)) {
        productRepository.deleteById(id);
    } else {
        throw new RuntimeException("削除対象の商品が見つかりません: ID " + id);
    }
}

    public Product update(Integer id, ProductForm form) {
    Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("更新対象の商品が見つかりません: ID " + id));

    existingProduct.setName(form.getProductName()); 
    existingProduct.setPrice(form.getProductPrice()); 
    existingProduct.setDescription(form.getDescription());
    existingProduct.setStock(form.getStock());
    existingProduct.setImageUrl(form.getImageUrl());

    Category category = categoryRepository.findById(form.getCategoryId())
            .orElseThrow(() -> new RuntimeException("更新対象のカテゴリが見つかりません: " + form.getCategoryId()));
    existingProduct.setCategory(category);

    return productRepository.save(existingProduct); 
}

    @Transactional 
public List<Product> saveAll(List<ProductForm> productForms) {
    List<Product> savedProducts = new ArrayList<>();
    for (ProductForm form : productForms) {
        Product product;
        if (form.getProductId() != null) {
            product = productRepository.findById(form.getProductId())
                    .orElseThrow(() -> new RuntimeException("更新対象の商品が見つかりません: ID " + form.getProductId()));
            updateProductFromForm(product, form);
        } else {
            product = new Product();
            updateProductFromForm(product, form);
        }
        savedProducts.add(productRepository.save(product));
    }
    return savedProducts;
}

private void updateProductFromForm(Product product, ProductForm form) {
    product.setName(form.getProductName());
    product.setDescription(form.getDescription());
    product.setPrice(form.getProductPrice());
    product.setStock(form.getStock());
    product.setImageUrl(form.getImageUrl());

    Category category = categoryRepository.findById(form.getCategoryId())
            .orElseThrow(() -> new RuntimeException("カテゴリが見つかりません: " + form.getCategoryId()));
    product.setCategory(category);
}
}
