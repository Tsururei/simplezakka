package com.example.simplezakka.controller;

import com.example.simplezakka.dto.order.OrderRequest;
import com.example.simplezakka.dto.order.UserOrderResponse;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.exception.UserNotFoundException;
import com.example.simplezakka.repository.UserRepository;
import com.example.simplezakka.service.JwtTokenProvider;
import com.example.simplezakka.service.UserCartService;
import com.example.simplezakka.service.UserOrderService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    public ResponseEntity<UserOrderResponse> placeOrder(
        @RequestParam Integer userId,
        @Valid @RequestBody OrderRequest orderRequest) {

        // ユーザー情報取得
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("ユーザーが見つかりません"));

        // 注文処理
        UserOrderResponse response = userOrderService.placeOrder(user.getUserId(), orderRequest);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleJsonParseError(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("リクエストボディの形式が正しくありません");
    }
}

