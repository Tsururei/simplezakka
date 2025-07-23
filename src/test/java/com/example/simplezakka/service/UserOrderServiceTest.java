package com.example.simplezakka.service;

import com.example.simplezakka.dto.cart.CartDto;
import com.example.simplezakka.dto.cart.CartGuest;
import com.example.simplezakka.dto.cart.CartItem;
import com.example.simplezakka.dto.order.CustomerInfo;
import com.example.simplezakka.dto.order.OrderRequest;
import com.example.simplezakka.dto.order.UserOrderResponse;
import com.example.simplezakka.entity.Order;
import com.example.simplezakka.entity.OrderDetail;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private DbCartrepository dbCartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCartService userCartService;

    @InjectMocks
    private UserOrderService userOrderService;

    private User user;
    private Product product;
    private CartItem cartItem;
    private CartGuest cart;
    private CustomerInfo customerInfo;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(userOrderService, "userCartService", userCartService);

        user = new User();
        user.setUserId(1);
        user.setUserName("テストユーザー");
        user.setUserEmail("test@example.com");

        product = new Product();
        product.setProductId(100);
        product.setName("商品A");
        product.setPrice(1000);
        product.setStock(10);

        cartItem = new CartItem();
        cartItem.setProductId(100);
        cartItem.setQuantity(2);
        cartItem.setName("商品A");
        cartItem.setPrice(1000);

        cart = new CartGuest();
        cart.setUserId(1);
        cart.setItems(new HashMap<>(Map.of("100", cartItem)));

        customerInfo = new CustomerInfo();
        customerInfo.setCustomerName("顧客A");
        customerInfo.setCustomerEmail("customer@example.com");
        customerInfo.setCustomerAddress("東京都");
        customerInfo.setShippingAddress("東京都A");
        customerInfo.setShippingName("配送先A");
        customerInfo.setPayMethod("代引き");

        orderRequest = new OrderRequest();
        orderRequest.setCustomerInfo(customerInfo);
    }

    @Test
    @DisplayName("正常に注文が確定し、レスポンスが返る")
    void placeOrder_Success() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userCartService.getCartFromDb(any())).thenReturn(cart);
        when(productRepository.findById(100)).thenReturn(Optional.of(product));
        when(productRepository.decreaseStock(100, 2)).thenReturn(1);
        when(orderRepository.save(any())).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setOrderId(999);
            return o;
        });

        // Act
        UserOrderResponse response = userOrderService.placeOrder(1, orderRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(999);
        assertThat(response.getUserName()).isEqualTo("テストユーザー");
        assertThat(response.getOrderItems()).hasSize(1);
        verify(orderRepository).save(any(Order.class));
        verify(userCartService).clearUserCart(any());
    }

    @Test
    @DisplayName("ユーザーが存在しない場合は例外がスローされる")
    void placeOrder_WhenUserNotFound_ShouldThrow() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userOrderService.placeOrder(1, orderRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ユーザーが存在しません");
    }

    @Test
    @DisplayName("カートが空の場合は例外がスローされる")
    void placeOrder_WhenCartEmpty_ShouldThrow() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userCartService.getCartFromDb(any())).thenReturn(new CartGuest());

        assertThatThrownBy(() -> userOrderService.placeOrder(1, orderRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("カートが空です");
    }

    @Test
    @DisplayName("商品が存在しない場合は例外がスローされる")
    void placeOrder_WhenProductNotFound_ShouldThrow() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userCartService.getCartFromDb(any())).thenReturn(cart);
        when(productRepository.findById(100)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userOrderService.placeOrder(1, orderRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("商品が見つかりません");
    }

    @Test
    @DisplayName("在庫が不足している場合は例外がスローされる")
    void placeOrder_WhenStockInsufficient_ShouldThrow() {
        product.setStock(1); // 在庫が足りない
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userCartService.getCartFromDb(any())).thenReturn(cart);
        when(productRepository.findById(100)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> userOrderService.placeOrder(1, orderRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("在庫が不足しています");
    }

    @Test
    @DisplayName("在庫更新に失敗した場合は例外がスローされる")
    void placeOrder_WhenStockUpdateFails_ShouldThrow() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userCartService.getCartFromDb(any())).thenReturn(cart);
        when(productRepository.findById(100)).thenReturn(Optional.of(product));
        when(productRepository.decreaseStock(100, 2)).thenReturn(0); // 更新失敗

        assertThatThrownBy(() -> userOrderService.placeOrder(1, orderRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("在庫の更新に失敗しました");
    }
}
