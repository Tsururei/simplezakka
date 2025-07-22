package com.example.simplezakka.controller;

import com.example.simplezakka.dto.cart.CartGuest;
import com.example.simplezakka.dto.cart.CartItemInfo;
import com.example.simplezakka.dto.cart.CartItemQuantityDto;
import com.example.simplezakka.service.UserCartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCartController.class)
class UserCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCartService userCartService;

    private final Integer userId = 1;

    private CartGuest createCart() {
        CartGuest cart = new CartGuest();
        cart.setUserId(userId);
        cart.setTotalQuantity(2);
        return cart;
    }

    @Nested
    @DisplayName("GET /api/usercart")
    class GetCartTests {
        @Test
        @DisplayName("ユーザーのカートを取得できること (200 OK)")
        void getCart_ShouldReturnCart() throws Exception {
            CartGuest cart = createCart();
            when(userCartService.getCartFromDb(any())).thenReturn(cart);

            mockMvc.perform(get("/api/usercart")
                            .param("userId", String.valueOf(userId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.totalQuantity").value(2));

            verify(userCartService).getCartFromDb(any());
        }
    }

    @Nested
    @DisplayName("POST /api/usercart")
    class AddItemTests {
        @Test
        @DisplayName("アイテムを追加すると更新されたカートが返る (200 OK)")
        void addItem_ShouldReturnUpdatedCart() throws Exception {
            CartGuest cart = createCart();
            when(userCartService.addItemToUserCart(anyInt(), anyInt(), any())).thenReturn(cart);

            String requestBody = """
                    { "productId": 101, "quantity": 2 }
                    """;

            mockMvc.perform(post("/api/usercart")
                            .param("userId", String.valueOf(userId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId));

            verify(userCartService).addItemToUserCart(eq(101), eq(2), any());
        }

        @Test
        @DisplayName("アイテム追加時にカートが存在しない場合は404")
        void addItem_WhenCartNotFound_ShouldReturnNotFound() throws Exception {
            when(userCartService.addItemToUserCart(anyInt(), anyInt(), any())).thenReturn(null);

            String requestBody = """
                    { "productId": 101, "quantity": 2 }
                    """;

            mockMvc.perform(post("/api/usercart")
                            .param("userId", String.valueOf(userId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isNotFound());

            verify(userCartService).addItemToUserCart(eq(101), eq(2), any());
        }
    }

    @Nested
    @DisplayName("PUT /api/usercart/items/{itemId}")
    class UpdateItemTests {
        @Test
        @DisplayName("カート内アイテムの数量を更新できる (200 OK)")
        void updateUserItem_ShouldReturnUpdatedCart() throws Exception {
            CartGuest cart = createCart();
            when(userCartService.updateUserItemQuantity(anyString(), anyInt(), any())).thenReturn(cart);

            String requestBody = """
                    { "quantity": 5 }
                    """;

            mockMvc.perform(put("/api/usercart/items/abc123")
                            .param("userId", String.valueOf(userId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalQuantity").value(2));

            verify(userCartService).updateUserItemQuantity(eq("abc123"), eq(5), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/usercart/items/{itemId}")
    class RemoveItemTests {
        @Test
        @DisplayName("カート内アイテムを削除できる (200 OK)")
        void removeUserItem_ShouldReturnUpdatedCart() throws Exception {
            CartGuest cart = createCart();
            when(userCartService.removeUserItemFromCart(anyString(), any())).thenReturn(cart);

            mockMvc.perform(delete("/api/usercart/items/abc123")
                            .param("userId", String.valueOf(userId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId));

            verify(userCartService).removeUserItemFromCart(eq("abc123"), any());
        }
    }
}
