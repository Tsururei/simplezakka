package com.example.simplezakka.controller;

import com.example.simplezakka.dto.order.*;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.exception.UserNotFoundException;
import com.example.simplezakka.repository.UserRepository;
import com.example.simplezakka.service.JwtTokenProvider;
import com.example.simplezakka.service.UserCartService;
import com.example.simplezakka.service.UserOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserOrderController.class)
public class UserOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserOrderService userOrderService;

    @MockBean
    private UserCartService userCartService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequest validOrderRequest;

    @BeforeEach
void setup() {
    CustomerInfo customerInfo = new CustomerInfo();
    customerInfo.setCustomerName("テスト太郎");
    customerInfo.setCustomerAddress("東京都テスト市1-2-3");
    customerInfo.setCustomerEmail("test@example.com");
    customerInfo.setShippingName("テスト太郎");
    customerInfo.setShippingAddress("東京都テスト市1-2-3");
    customerInfo.setPayMethod("クレジットカード");

    OrderItemDto item = new OrderItemDto();
    item.setProductName("テスト商品");
    item.setQuantity(2);
    item.setUnitPrice(BigDecimal.valueOf(100));  // 追加
    item.setSubtotal(BigDecimal.valueOf(200));   // 追加

    validOrderRequest = new OrderRequest();
    validOrderRequest.setCustomerInfo(customerInfo);
    validOrderRequest.setItems(List.of(item));
}

@Test
void 正常注文処理_ステータス200でUserOrderResponse返却() throws Exception {
    User user = new User();
    user.setUserId(1);  // 追加（Userのidセット）
    Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user));

    UserOrderResponse response = new UserOrderResponse();
    response.setOrderId(1);
    response.setStatus("SUCCESS");

    Mockito.when(userOrderService.placeOrder(eq(1), any(OrderRequest.class))).thenReturn(response);

    mockMvc.perform(post("/api/user/orders")
            .param("userId", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validOrderRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value(1))
        .andExpect(jsonPath("$.status").value("SUCCESS"));
}

    @Test
    void ユーザーが存在しない場合_UserNotFoundExceptionがスローされる() throws Exception {
        Mockito.when(userRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/user/orders")
                .param("userId", "999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrderRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ユーザーが見つかりません"));
    }

    @Test
    void userIdがnullの場合_400BadRequest() throws Exception {
        mockMvc.perform(post("/api/user/orders")
                // userId を指定しない
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrderRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void OrderRequestがnullの場合_400BadRequest() throws Exception {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/user/orders")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")) // 空ボディ
                .andExpect(status().isBadRequest());
    }

    @Test
    void OrderRequestがバリデーションエラーの場合_400BadRequest() throws Exception {
        // customerInfo が null → バリデーションエラーになる想定
        OrderRequest invalidRequest = new OrderRequest();
        invalidRequest.setCustomerInfo(null); // @NotNull 違反
        invalidRequest.setItems(List.of());

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/user/orders")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
