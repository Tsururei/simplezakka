package com.example.simplezakka.repository;

import com.example.simplezakka.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // Order エンティティ内の orderId を使って検索
    List<OrderDetail> findByOrder_OrderId(Integer orderId);
}

