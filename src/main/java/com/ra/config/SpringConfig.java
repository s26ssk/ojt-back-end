
package com.ra.config;

import com.ra.model.*;
import com.ra.repository.UserRepository;
import com.ra.repository.WareHouseRepository;
import com.ra.service.inter.EmailService;
import com.ra.service.inter.OrderHistoryService;
import com.ra.service.inter.OrderService;
import com.ra.util.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableScheduling
public class SpringConfig {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WareHouseRepository wareHouseRepository;
    @Autowired
    private OrderHistoryService orderHistoryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Scheduled(fixedDelay = 3600)
    public void coordinateTask() {
        List<Orders> orders = orderService.findTop100ByOrderStatus(OrderStatus.NEW_ORDER);
        if (!orders.isEmpty()) {
            for (Orders order : orders) {
                List<Warehouse> warehouses = wareHouseRepository.availableWareHouse();
                try {
                    orderService.autoCoordinate(order, warehouses);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (CustomException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 10-22 * * ?")
    public void deliverFailOrderTask() throws CustomException {
        Users users = userRepository.findByEmail("system");
        List<Orders> orders = orderService.findTop100ByOrderStatus(OrderStatus.DELIVERY_FAIL);
        for (Orders order : orders) {
            List<OrdersHistory> ordersHistories = orderHistoryService.findAllByOrdersAndStatus(order, OrderStatus.DELIVERY_FAIL);
            if (ordersHistories.size() >= 3) {
                orderService.returnOrder(order, users);
                emailService.sendMailtoUserOrder(order.getOrderCode());
            }
        }
    }
}
