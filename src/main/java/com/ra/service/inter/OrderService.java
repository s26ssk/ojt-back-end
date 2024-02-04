package com.ra.service.inter;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.ra.dto.request.DeliveryRequest;
import com.ra.dto.request.OrderRequest;
import com.ra.dto.response.OrdersResponse;
import com.ra.model.*;
import com.ra.util.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface OrderService {

    ResponseEntity<Orders> deliveryOrder(DeliveryRequest deliveryRequest, Users users) throws CustomException;

    Orders editOrder(String orderCode, OrderRequest orderRequest, Users users) throws CustomException, JsonMappingException;

    Orders save(Orders orders);

    Orders findById(String orderId) throws CustomException;

    List<Orders> findTop100ByOrderStatus(OrderStatus orderStatus);

    OrdersResponse coordinateToWareHouse(Orders orders, Users users);

    void autoCoordinate(Orders orders, List<Warehouse> warehouses) throws IOException, InterruptedException, CustomException, CustomException;

    Orders createOrder(OrderRequest orderRequest, Users users) throws CustomException;

    ResponseEntity<Map<String, String>> importExcelOrder(MultipartFile multipartFile, Users users);

    void deleteOrderAndHistory(String orderId) throws CustomException;

    Page<Orders> getAllOrders(Pageable pageable, Users users);

    Page<Orders> searchOrders(String keyword, Date createDate, OrderStatus orderStatus, String warehouseCode, Users users, Pageable pageable);

    Orders returnOrder(Orders orders, Users users);

    Orders createSingleOrder(OrderRequest orders, Users users) throws CustomException;

    Page<Orders> findOrdersByOrderStatusAndUser(Pageable pageable, OrderStatus status, Users users);

}
