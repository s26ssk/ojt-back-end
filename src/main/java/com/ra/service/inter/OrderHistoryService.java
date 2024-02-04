package com.ra.service.inter;

import com.ra.model.OrderStatus;
import com.ra.model.Orders;
import com.ra.model.OrdersHistory;
import com.ra.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OrderHistoryService {
    OrdersHistory save(OrdersHistory ordersHistory);

    OrdersHistory buildHistoryOrderHistory(Orders order, Users users, OrderStatus status);


    List<OrdersHistory> findAllByOrdersAndStatus(Orders orders, OrderStatus orderStatus);

    OrdersHistory findByStatusAndOrders(OrderStatus status, Orders orders);

    void deleteOrderHistoryByOrder(Orders order);


    Page<OrdersHistory> getAllOrderHistory(Pageable pageable, Users users);


}
