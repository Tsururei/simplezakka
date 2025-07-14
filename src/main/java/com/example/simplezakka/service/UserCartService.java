package com.example.simplezakka.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simplezakka.dto.cart.Cart;
import com.example.simplezakka.dto.cart.CartDto;
import com.example.simplezakka.dto.cart.CartItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.DbCartrepository;
import com.example.simplezakka.repository.ProductRepository;

@Service
public class UserCartService {

    private final ProductRepository productRepository;
    private final DbCartrepository dbCartRepository;
    @Autowired
    public UserCartService(DbCartrepository dbCartRepository, ProductRepository productRepository) {
        this.dbCartRepository = dbCartRepository;
        this.productRepository = productRepository;
    }
    public Cart getCartFromDb(CartDto cartDto) {
        Integer userId = cartDto.getUserId();
        Optional<Cart> cartOpt = dbCartRepository.findByUserId(userId);
        Cart cart = cartOpt.get();
        return cart;
    }
    
    public Cart addItemToUserCart(Integer productId, Integer quantity, CartDto cartDto) {
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            Cart cart = getCartFromDb(cartDto);
            
            CartItem item = new CartItem();
            item.setProductId(product.getProductId());
            item.setName(product.getName());
            item.setPrice(product.getPrice());
            item.setImageUrl(product.getImageUrl());
            item.setQuantity(quantity);
            
            cart.addItem(item);
            dbCartRepository.save(cart);
            
            return cart;
        }
        
        return null;
    }
    
    public Cart updateUserItemQuantity(String itemId, Integer quantity, CartDto cartDto) {
        Cart cart = getCartFromDb(cartDto);
        cart.updateQuantity(itemId, quantity);
        dbCartRepository.save(cart);
        return cart;
    }
    
    public Cart removeUserItemFromCart(String itemId, CartDto cartDto) {
        Cart cart = getCartFromDb(cartDto);
        cart.removeItem(itemId);
        dbCartRepository.save(cart);
        return cart;
    }
    
    public Cart clearUserCart(CartDto cartDto) {
        Cart cart = getCartFromDb(cartDto);
        cart.clear();
        dbCartRepository.save(cart);
        return cart;
    }
} 
