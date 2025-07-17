package com.example.simplezakka.controller;

import com.example.simplezakka.dto.cart.CartGuest;
import com.example.simplezakka.dto.cart.CartDto;
import com.example.simplezakka.dto.cart.CartItemInfo;
import com.example.simplezakka.dto.cart.CartItemQuantityDto;
import com.example.simplezakka.entity.Cart;
import com.example.simplezakka.entity.CartItemEntity;
import com.example.simplezakka.service.UserCartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usercart")
public class UserCartController {

    private final UserCartService userCartService;
    @Autowired
    public UserCartController(UserCartService userCartService) {
        this.userCartService = userCartService;
    }
    
    @GetMapping
    public ResponseEntity<CartGuest> getCart(@RequestParam Integer userId) {
        CartDto cartDto = new CartDto();
        cartDto.setUserId(userId);
        CartGuest cart = userCartService.getCartFromDb(cartDto);
        return ResponseEntity.ok(cart);
    }
    
    @PostMapping
    public ResponseEntity<CartGuest> addItem(@Valid @RequestBody CartItemInfo cartItemInfo, @RequestParam Integer userId) {
        CartDto cartDto = new CartDto();
        cartDto.setUserId(userId);
        CartGuest cart = userCartService.addItemToUserCart(
                cartItemInfo.getProductId(),
                cartItemInfo.getQuantity(),
                cartDto
        );
        
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(cart);
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartGuest> updateUserItem(
            @PathVariable String itemId,
            @Valid @RequestBody CartItemQuantityDto quantityDto, @RequestParam Integer userId) {
        CartDto cartDto = new CartDto();
        cartDto.setUserId(userId);
        CartGuest cart = userCartService.updateUserItemQuantity(itemId, quantityDto.getQuantity(), cartDto);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartGuest> removeUserItem(@PathVariable String itemId, @RequestParam Integer userId) {
        CartDto cartDto = new CartDto();
        cartDto.setUserId(userId);
        CartGuest cart = userCartService.removeUserItemFromCart(itemId, cartDto);
        return ResponseEntity.ok(cart);
    }    
}