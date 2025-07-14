package com.example.simplezakka.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.text.html.parser.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.simplezakka.entity.Cart;
import com.example.simplezakka.entity.CartItemEntity;
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

    private CartItemEntity convertToCartItemEntity(com.example.simplezakka.dto.cart.CartItem dtoItem, Cart cart) {
        CartItemEntity entity = new CartItemEntity();
        entity.setCart(cart);
        entity.setProductId(dtoItem.getProductId());
        entity.setCartQuantity(dtoItem.getQuantity());
        return entity;
}


    private List<CartItemEntity> convertToCartItemEntitiy(CartGuest cartGuest, Cart cart) {
        List<com.example.simplezakka.dto.cart.CartItem> dtoItems = new ArrayList<>(cartGuest.getItems().values());

        return dtoItems.stream()
        .map(dtoItem -> convertToCartItemEntity(dtoItem, cart))
        .collect(Collectors.toList());
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
            Cart cart = convertToCartEntity(cartGuest);
            dbCartRepository.save(cart);
            return cartGuest;
        }    
        return null;
    }
    
    private Cart convertToCartEntity(CartGuest cartGuest) {
        Cart cart = new Cart();
        cart.setCartId(cartGuest.getCartId());
        cart.setUserId(cartGuest.getUserId());
        cart.setCreatedAt(cartGuest.getCreatedAt());
        cart.setUpdatedAt(cartGuest.getUpdatedAt());

        List<CartItemEntity> cartItems = cartGuest.getItems().values().stream()
        .map(dtoItem -> {
        CartItemEntity entityItem = new CartItemEntity();
        entityItem.setCart(cart); 
        entityItem.setProductId(dtoItem.getProductId());
        entityItem.setCartQuantity(dtoItem.getQuantity());
        return entityItem;
        })
        .collect(Collectors.toList());

        cart.setItems(cartItems);
        return cart;
        }



    public CartGuest updateUserItemQuantity(String itemId, Integer quantity, CartDto cartDto) {
        CartGuest cartGuest = getCartFromDb(cartDto);
        cartGuest.updateQuantity(itemId, quantity);
        Cart cart = convertToCartEntity(cartGuest);
        dbCartRepository.save(cart);
        return cartGuest;
    }
    
    public CartGuest removeUserItemFromCart(String itemId, CartDto cartDto) {
        CartGuest cartGuest = getCartFromDb(cartDto);
        cartGuest.removeItem(itemId);
        Cart cart = convertToCartEntity(cartGuest);
        dbCartRepository.save(cart);
        return cartGuest;
    }
    
    public CartGuest clearUserCart(CartDto cartDto) {
        CartGuest cartGuest = getCartFromDb(cartDto);
        cartGuest.clear();
        Cart cart = convertToCartEntity(cartGuest);
        dbCartRepository.save(cart);
        return cartGuest;
    }
} 
