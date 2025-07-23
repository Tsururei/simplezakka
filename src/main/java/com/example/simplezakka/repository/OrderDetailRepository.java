package com.example.simplezakka.repository;

import com.example.simplezakka.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // ✅ Order エンティティの orderId を使って関連する明細一覧を取得
    List<OrderDetail> findByOrder_OrderId(Integer orderId);
}
