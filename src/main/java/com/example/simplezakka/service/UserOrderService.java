package com.example.simplezakka.service;

import com.example.simplezakka.dto.cart.CartGuest;
import com.example.simplezakka.dto.cart.CartItem;
import com.example.simplezakka.dto.cart.CartDto;
import com.example.simplezakka.dto.order.OrderItemDto;
import com.example.simplezakka.dto.order.OrderRequest;
import com.example.simplezakka.dto.order.OrderResponse;
import com.example.simplezakka.dto.order.UserOrderResponse;
import com.example.simplezakka.entity.Order;
import com.example.simplezakka.entity.OrderDetail;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.OrderDetailRepository;
import com.example.simplezakka.repository.OrderRepository;
import com.example.simplezakka.repository.ProductRepository;
import com.example.simplezakka.repository.DbCartrepository;
import com.example.simplezakka.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserOrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final DbCartrepository dbCartRepository;
    private final UserRepository userReopsitory;

    @Autowired
    private UserCartService userCartService;
    public UserOrderService(
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository,
            DbCartrepository dbCartRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.dbCartRepository = dbCartRepository;
        this.userReopsitory = userRepository;

    }

    @Transactional
public UserOrderResponse placeOrder(Integer userId, OrderRequest orderRequest) {
    // ユーザー取得
    User user = userReopsitory.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません: " + userId));

    // カート取得
    CartDto cartDto = new CartDto();
    cartDto.setUserId(userId);
    CartGuest cart = userCartService.getCartFromDb(cartDto);

    if (cart == null || cart.getItems().isEmpty()) {
        throw new IllegalStateException("カートが空です");
    }

    // 在庫確認と明細作成
    List<OrderDetail> orderDetails = new ArrayList<>();
    BigDecimal totalPrice = BigDecimal.ZERO;

    for (CartItem item : cart.getItems().values()) {
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new IllegalStateException("商品が見つかりません: " + item.getProductId()));

        if (product.getStock() < item.getQuantity()) {
            throw new IllegalStateException("在庫が不足しています: " + product.getName());
        }

        // 在庫減算
        int updated = productRepository.decreaseStock(product.getProductId(), item.getQuantity());
        if (updated != 1) {
            throw new IllegalStateException("在庫の更新に失敗しました: " + product.getName());
        }

        OrderDetail detail = new OrderDetail();
        detail.setProductId(product.getProductId());
        detail.setProductName(product.getName());
        detail.setQuantity(item.getQuantity());
        detail.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
        detail.setPrice(BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())));
        orderDetails.add(detail);

        totalPrice = totalPrice.add(detail.getPrice());
    }

    // 注文情報作成
    Order order = new Order();
    order.setOrderDate(LocalDateTime.now());
    order.setTotalAmount(totalPrice.intValue());
    order.setCustomerName(user.getUserName());
    order.setCustomerEmail(user.getUserEmail());
    order.setShippingAddress(user.getUserAddress());
    order.setStatus("PENDING");

    for (OrderDetail detail : orderDetails) {
        order.addOrderDetail(detail);
    }

    cartDto.setUserId(userId);
    Order savedOrder = orderRepository.save(order);
    userCartService.clearUserCart(cartDto);  // カートクリア

    // DTO変換して返す
    List<OrderItemDto> itemDtos = orderDetails.stream()
        .map(d -> new OrderItemDto(
                d.getProductName(),
                d.getQuantity(),
                d.getUnitPrice(),
                d.getPrice()
        ))
        .toList();

    return UserOrderResponse.builder()
            .orderId(savedOrder.getOrderId())
            .orderDate(savedOrder.getOrderDate())
            .totalPrice(BigDecimal.valueOf(savedOrder.getTotalAmount()))
            .status(savedOrder.getStatus())
            .userName(user.getUserName())
            .userEmail(user.getUserEmail())
            .shippingAddress(user.getUserAddress())
            .orderItems(itemDtos)
            .build();
}

    public OrderResponse placeOrder(User user, CartGuest cart, OrderRequest orderRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'placeOrder'");
    }
}