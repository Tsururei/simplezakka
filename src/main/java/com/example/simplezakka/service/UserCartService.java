package com.example.simplezakka.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.simplezakka.entity.Cart;
import com.example.simplezakka.dto.cart.CartDto;
import com.example.simplezakka.dto.cart.CartGuest;
import com.example.simplezakka.dto.cart.CartItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.DbCartrepository;
import com.example.simplezakka.repository.ProductRepository;
import com.example.simplezakka.repository.UserRepository;

import jakarta.persistence.Column;

@Service
public class UserCartService {

    private final ProductRepository productRepository;
    private final DbCartrepository dbCartRepository;
    @Autowired
    public UserCartService(DbCartrepository dbCartRepository, ProductRepository productRepository) {
        this.dbCartRepository = dbCartRepository;
        this.productRepository = productRepository;
    }
    public CartGuest getCartFromDb(CartDto cartDto) {
        Integer userId = cartDto.getUserId();
        Optional<Cart> cartOpt = dbCartRepository.findByUserId(userId);
        Cart cart = cartOpt.get();
        return convertToDto(cartOpt.get());
    }

    private CartGuest convertToDto(Cart cart) {
        CartGuest cartGuest = new CartGuest();
        cartGuest.setCartId(cart.getCartId());
        cartGuest.setUserId(cart.getUserId());
        cartGuest.setCreatedAt(cart.getCreatedAt());
        cartGuest.setUpdatedAt(cart.getUpdatedAt());
        return cartGuest;
    }

    public CartGuest addItemToUserCart(Integer productId, Integer quantity, CartDto cartDto) {
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            CartGuest cartGuest = getCartFromDb(cartDto);
            
            CartItem item = new CartItem();
            item.setProductId(product.getProductId());
            item.setName(product.getName());
            item.setPrice(product.getPrice());
            item.setImageUrl(product.getImageUrl());
            item.setQuantity(quantity);
            
            cartGuest.addItem(item);
            return convertToEntity(cartGuest);
            
    private Cart convertToEntity(CartGuest cartGuest) {
            Cart cart = new Cart();
            cart.setCartId(cartGuest.getCartId());
            cart.setUserId(cartGuest.getUserId());
            cart.setCreatedAt(cartGuest.getCreatedAt());
            cart.setUpdatedAt(cartGuest.getUpdatedAt());
            dbCartRepository.save(cart);
            return cart;
        }
        
        return null;
    }
    
    public CartGuest updateUserItemQuantity(String itemId, Integer quantity, CartDto cartDto) {
        CartGuest cart = getCartFromDb(cartDto);
        cart.updateQuantity(itemId, quantity);
        dbCartRepository.save(cart);
        return cart;
    }
    
    public CartGuest removeUserItemFromCart(String itemId, CartDto cartDto) {
        CartGuest cart = getCartFromDb(cartDto);
        cart.removeItem(itemId);
        dbCartRepository.save(cart);
        return cart;
    }
    
    public CartGuest clearUserCart(CartDto cartDto) {
        CartGuest cart = getCartFromDb(cartDto);
        cart.clear();
        dbCartRepository.save(cart);
        return cart;
    }
} 
