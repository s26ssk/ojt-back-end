package com.ra.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.ra.dto.request.DeliveryRequest;
import com.ra.dto.request.OrderRequest;
import com.ra.model.OrderStatus;
import com.ra.model.Orders;
import com.ra.model.OrdersHistory;
import com.ra.security.principal.UserPrinciple;
import com.ra.service.impl.LabelService;
import com.ra.service.inter.EmailService;
import com.ra.service.inter.OrderHistoryService;
import com.ra.service.inter.OrderService;
import com.ra.util.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private LabelService labelService;
    @Autowired
    private OrderHistoryService orderHistoryService;
    @Autowired
    private EmailService emailService;

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getListOrder(
            @PageableDefault(page = 0, size = 5)
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sortBy", required = false, defaultValue = "orderCode") String sortBy,
            @RequestParam(name = "order", required = false, defaultValue = "asc") String order) throws CustomException {

        Pageable pageable = PageRequest.of(page, 5, Sort.by(order.equals("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy)));
        Page<Orders> orderPage = orderService.getAllOrders(pageable, userPrinciple().getUser());

        Map<String, Object> data = new HashMap<>();
        data.put("orders", orderPage.getContent());
        data.put("sizePage", orderPage.getSize());
        data.put("totalElement", orderPage.getTotalElements());
        data.put("totalPages", orderPage.getTotalPages());
        data.put("currentPage", orderPage.getNumber());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/orders-history")
    public ResponseEntity<Map<String, Object>> getListOrderHistory(
            @PageableDefault(page = 0, size = 5)
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sortBy", required = false, defaultValue = "updateAt") String sortBy,
            @RequestParam(name = "order", required = false, defaultValue = "desc") String order) throws CustomException {

        Pageable pageable = PageRequest.of(page, 5, Sort.by(order.equals("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy)));
        Page<OrdersHistory> orderHistoryPage = orderHistoryService.getAllOrderHistory(pageable, userPrinciple().getUser());

        Map<String, Object> data = new HashMap<>();
        data.put("ordersHistory", orderHistoryPage.getContent());
        data.put("sizePage", orderHistoryPage.getSize());
        data.put("totalElement", orderHistoryPage.getTotalElements());
        data.put("totalPages", orderHistoryPage.getTotalPages());
        data.put("currentPage", orderHistoryPage.getNumber());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }


    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) throws CustomException {
        return new ResponseEntity<>(orderService.createSingleOrder(request, userPrinciple().getUser()), HttpStatus.OK);
    }

    @PostMapping("/import-excel-order")
    public ResponseEntity<?> importOrder(@RequestBody MultipartFile multipartFile) throws CustomException {
        return orderService.importExcelOrder(multipartFile, userPrinciple().getUser());
    }

    @GetMapping("/create-label/{orderCode}")
    public ResponseEntity<byte[]> createLabel(@PathVariable String orderCode) {
        byte[] label = labelService.generateLabel(orderCode);
        if (label != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "label.pdf");
            return new ResponseEntity<>(label, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) throws CustomException {
        if(orderService.findById(orderId).getOrderStatus() == OrderStatus.NEW_ORDER){
            orderService.deleteOrderAndHistory(orderId);
        }else {
            return new ResponseEntity<>("Chỉ đơn hàng đang chờ xác thực mới được xóa!", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    private UserPrinciple userPrinciple() throws CustomException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        if (userPrinciple == null ){
            throw new CustomException("Không tìm thấy users", HttpStatus.UNAUTHORIZED);
        }
        return userPrinciple;
    }

    @GetMapping("/orders/search")
    public ResponseEntity<Map<String, Object>> searchOrders(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "time", required = false) String timeString,
            @RequestParam(name = "warehouse", required = false) String warehouse,
            @PageableDefault(page = 0, size = 5) Pageable pageable) throws CustomException {
        OrderStatus orderStatus = null;
        if (status != null && !status.isEmpty()) {
            orderStatus = OrderStatus.valueOf(status);
        }

        Date time = null;
        if (timeString != null && !timeString.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                time = dateFormat.parse(timeString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Page<Orders> orderPage = orderService.searchOrders(keyword, time, orderStatus, warehouse,userPrinciple().getUser(), pageable);

        Map<String, Object> data = new HashMap<>();
        data.put("orders", orderPage.getContent());
        data.put("sizePage", orderPage.getSize());
        data.put("totalElement", orderPage.getTotalElements());
        data.put("totalPages", orderPage.getTotalPages());
        data.put("currentPage", orderPage.getNumber());

        return ResponseEntity.ok(data);
    }

    @GetMapping("/orders-status")
    public ResponseEntity<Map<String, Object>> getOrdersByStatus(@RequestParam(name = "status", required = false, defaultValue = "") OrderStatus status,
                                                                 @PageableDefault(page = 0, size = 5) Pageable pageable) throws CustomException {

        Page<Orders> orders = orderService.findOrdersByOrderStatusAndUser(pageable, status, userPrinciple().getUser());

        Map<String, Object> data = new HashMap<>();
        data.put("orders", orders.getContent());
        data.put("sizePage", orders.getSize());
        data.put("totalElement", orders.getTotalElements());
        data.put("totalPages", orders.getTotalPages());
        data.put("currentPage", orders.getNumber());

        return ResponseEntity.ok(data);
    }

    @PatchMapping("/order/delivery")
    private ResponseEntity<Orders> confirmDelivery(@RequestBody DeliveryRequest deliveryRequest) throws CustomException {
        return orderService.deliveryOrder(deliveryRequest, userPrinciple().getUser());
    }

    @PutMapping("/order/{orderCode}")
    private ResponseEntity<Orders> editOrder(@RequestBody OrderRequest orderRequest, @PathVariable String orderCode) throws CustomException, JsonMappingException {
            Orders orders = orderService.editOrder(orderCode, orderRequest, userPrinciple().getUser());
            return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PostMapping("/send-email-on-fail/{orderCode}")
    private ResponseEntity<String> sendEmailOnFail (@PathVariable String orderCode) throws CustomException {
        return new ResponseEntity<>(emailService.sendMailtoUserOrder(orderCode),HttpStatus.OK);
    }
}
