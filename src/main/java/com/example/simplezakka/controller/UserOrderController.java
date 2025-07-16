package com.example.simplezakka.controller;

import com.example.simplezakka.dto.cart.CartDto;
import com.example.simplezakka.dto.cart.CartGuest;
import com.example.simplezakka.dto.order.OrderRequest;
import com.example.simplezakka.dto.order.OrderResponse;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.exception.CartNotFoundException;
import com.example.simplezakka.exception.UserNotFoundException;
import com.example.simplezakka.repository.UserRepository;
import com.example.simplezakka.service.JwtTokenProvider;
import com.example.simplezakka.service.UserCartService;
import com.example.simplezakka.service.UserOrderService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/orders")
public class UserOrderController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserCartService userCartService;
    private final UserOrderService userOrderService;
    private final UserRepository userRepository;

    @Autowired
    public UserOrderController(
        JwtTokenProvider jwtTokenProvider,
        UserCartService userCartService,
        UserOrderService userOrderService,
        UserRepository userRepository
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userCartService = userCartService;
        this.userOrderService = userOrderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody OrderRequest orderRequest) {

        // JWTからユーザーIDを取得
        String cleanedToken = token.replace("Bearer ", "");
        Integer userId = jwtTokenProvider.getUserIdFromToken(cleanedToken);

        // ユーザー情報取得
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("ユーザーが見つかりません"));

        // カート取得
        CartDto cartDto = new CartDto();
        cartDto.setUserId(userId);
        CartGuest cart = userCartService.getCartFromDb(cartDto);

        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartNotFoundException("カートが空または見つかりません");
        }

        // 注文処理
        OrderResponse response = userOrderService.placeOrder(user, cart, orderRequest);

        return ResponseEntity.ok(response);
    }

}

