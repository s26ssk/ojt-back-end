package com.ra.service.impl;

import com.ra.config.Config;
import com.ra.model.OrderStatus;
import com.ra.model.Orders;
import com.ra.model.OrdersHistory;
import com.ra.model.Users;
import com.ra.repository.OrderHistoryRepository;
import com.ra.service.inter.OrderHistoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;


    @Override
    public OrdersHistory save(OrdersHistory ordersHistory) {
        return orderHistoryRepository.save(ordersHistory);
    }

    @Override
    public OrdersHistory buildHistoryOrderHistory(Orders order, Users users, OrderStatus status) {
        return OrdersHistory.builder()
                .orders(order)
                .user(users)
                .status(status)
                .updateAt(Config.date())
                .build();
    }

    @Override
    public List<OrdersHistory> findAllByOrdersAndStatus(Orders orders, OrderStatus orderStatus) {
        return orderHistoryRepository.findAllByOrdersAndStatus(orders, orderStatus);
    }

    @Override
    public OrdersHistory findByStatusAndOrders(OrderStatus status, Orders orders) {
        return orderHistoryRepository.findByStatusAndOrders2(status, orders);
    }


    @Override
    @Transactional
    public void deleteOrderHistoryByOrder(Orders order) {
        orderHistoryRepository.deleteByOrders(order);
    }


    @Override
    public Page<OrdersHistory> getAllOrderHistory(Pageable pageable, Users user) {
        return orderHistoryRepository.findAllByUser(pageable, user);
    }

}
