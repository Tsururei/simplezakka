package com.example.simplezakka.controller;

import com.example.simplezakka.dto.order.OrderSummaryDto;
import com.example.simplezakka.dto.order.OrderDetailDto;
import com.example.simplezakka.dto.order.OrderStatusUpdateRequest;
import com.example.simplezakka.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // 注文一覧取得
    @GetMapping
    public ResponseEntity<List<OrderSummaryDto>> getOrderList() {
        List<OrderSummaryDto> orders = adminOrderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // 注文詳細取得
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable String orderId) {
        OrderDetailDto detail = adminOrderService.getOrderDetail(orderId);
        return ResponseEntity.ok(detail);
    }

    // 注文ステータス更新
    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody OrderStatusUpdateRequest request
    ) {
        adminOrderService.updateOrderStatus(orderId, request.getNewStatus());
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
