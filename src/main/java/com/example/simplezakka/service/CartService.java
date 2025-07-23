package com.example.simplezakka.service;

import com.example.simplezakka.dto.cart.CartGuest;
import com.example.simplezakka.dto.cart.CartItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "cart";
    private final ProductRepository productRepository;
    
    @Autowired
    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public CartGuest getCartFromSession(HttpSession session) {
        CartGuest cart = (CartGuest) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new CartGuest();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }
    
    public CartGuest addItemToCart(Integer productId, Integer quantity, HttpSession session) {
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            CartGuest cart = getCartFromSession(session);
            
            CartItem item = new CartItem();
            item.setProductId(product.getProductId());
            item.setName(product.getName());
            item.setPrice(product.getPrice());
            item.setImageUrl(product.getImageUrl());
            item.setQuantity(quantity);
            
            cart.addItem(item);
            session.setAttribute(CART_SESSION_KEY, cart);
            
            return cart;
        }
        
        return null;
    }
    
    public CartGuest updateItemQuantity(String itemId, Integer quantity, HttpSession session) {
        CartGuest cart = getCartFromSession(session);
        cart.updateQuantity(itemId, quantity);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }
    
    public CartGuest removeItemFromCart(String itemId, HttpSession session) {
        CartGuest cart = getCartFromSession(session);
        cart.removeItem(itemId);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }
    
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}
