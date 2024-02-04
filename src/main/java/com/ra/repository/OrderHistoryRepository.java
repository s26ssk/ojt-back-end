package com.ra.repository;

import com.ra.dto.response.OrderHistoryResponse;
import com.ra.model.OrderStatus;
import com.ra.model.Orders;
import com.ra.model.OrdersHistory;
import com.ra.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrdersHistory, Long> {
    List<OrdersHistory> findAllByOrdersOrderByUpdateAtDesc(Orders orders);

    List<OrdersHistory> findAllByOrdersAndStatus(Orders orders, OrderStatus status);

    @Query("SELECT oh from OrdersHistory oh where oh.orderCode = :orders and oh.status = :status")
    OrdersHistory findByStatusAndOrders2(@Param("status") OrderStatus status, @Param("orders") Orders orders);
    OrdersHistory findByStatusAndOrders(OrderStatus status, Orders orders);
    void deleteByOrders(Orders order);
    Page<OrdersHistory> findAllByUser(Pageable pageable, Users users);
}
