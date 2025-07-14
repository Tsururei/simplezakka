package com.example.simplezakka.service;

import com.example.simplezakka.dto.order.OrderDetailDto;
import com.example.simplezakka.dto.order.OrderItemDto;
import com.example.simplezakka.dto.order.OrderSummaryDto;
import com.example.simplezakka.entity.Order;
import com.example.simplezakka.entity.OrderDetail;
import com.example.simplezakka.repository.OrderDetailRepository;
import com.example.simplezakka.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    // ✅ 全注文一覧を取得
    public List<OrderSummaryDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> new OrderSummaryDto(
                        String.valueOf(order.getOrderId()),
                        order.getCustomerName(),
                        order.getOrderDate().toString(),
                        BigDecimal.valueOf(order.getTotalAmount()),
                        order.getStatus()
                ))
                .collect(Collectors.toList());
    }

    // ✅ 注文詳細を取得
    public OrderDetailDto getOrderDetail(String orderId) {
        Order order = orderRepository.findById(Integer.valueOf(orderId)) // IDはIntegerのため変換
                .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません"));

        // ✅ 正しいリポジトリ呼び出し
        List<OrderDetail> details = orderDetailRepository.findByOrder_OrderId(order.getOrderId());

        List<OrderItemDto> items = details.stream()
                .map(detail -> {
                    String productName = "商品名未取得"; // あとでProductRepositoryで取得可
                    return new OrderItemDto(
                            productName,
                            detail.getQuantity(),
                            detail.getUnitPrice(),
                            detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()))
                    );
                })
                .collect(Collectors.toList());

        return new OrderDetailDto(
                String.valueOf(order.getOrderId()),
                order.getCustomerName(),
                order.getShippingAddress(),
                items,
                BigDecimal.valueOf(order.getTotalAmount()),
                order.getStatus()
        );
    }

    // ✅ 注文ステータスを更新
    public void updateOrderStatus(String orderId, String newStatus) {
        Order order = orderRepository.findById(Integer.valueOf(orderId))
                .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません"));

        // ✅ setStatus() が正しい setter メソッド名
        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}
