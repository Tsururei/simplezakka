package com.example.simplezakka.dto.product;

public class ProductForm {
    private String productName;
    private String description;
    private Integer stock;
    private Integer productPrice;
    private String imageUrl;
    private String categoryId;
    private Integer productId;

    public ProductForm() {}

    public ProductForm(String productName, String description, Integer productPrice, Integer stock, String imageUrl, String categoryId, Integer productId) {
        this.productName = productName;
        this.description = description;
        this.productPrice = productPrice;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
}
