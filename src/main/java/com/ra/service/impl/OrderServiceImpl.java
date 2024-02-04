package com.ra.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.ra.config.Config;
import com.ra.config.Validator;
import com.ra.dto.request.DeliveryRequest;
import com.ra.dto.request.OrderRequest;
import com.ra.dto.response.OrdersResponse;
import com.ra.model.*;
import com.ra.repository.OrdersRepository;
import com.ra.repository.UserRepository;
import com.ra.service.inter.OrderHistoryService;
import com.ra.service.inter.OrderService;
import com.ra.service.inter.WarehouseService;
import com.ra.dto.response.DistanceMatrixResponse;
import com.ra.util.exception.CustomException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ra.config.Config.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderHistoryService orderHistoryService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Orders> deliveryOrder(DeliveryRequest deliveryRequest, Users users) throws CustomException {
        Orders orders = findById(deliveryRequest.getOrderCode());
        Warehouse warehouse = warehouseService.findById(orders.getWarehouse().getWarehouseCode());
        if (orders.getOrderStatus().equals(OrderStatus.IN_STORED) || orders.getOrderStatus().equals(OrderStatus.DELIVERY_FAIL)) {
            if (orders.getFailCount() >= 3) {
                throw new CustomException(" SYSS-1101", HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
            }
            OrdersHistory ordersHistory = orderHistoryService.buildHistoryOrderHistory(orders, users, null);
            switch (deliveryRequest.getStatus()) {
                case "DELIVERY_FAIL" -> {
                    orders.setOrderStatus(OrderStatus.DELIVERY_FAIL);
                    orders.setFailCount(orders.getFailCount() + 1);
                    switch (deliveryRequest.getComment()) {
                        case "LD1" -> ordersHistory.setComment(FailCause.LD1);
                        case "LD2" -> ordersHistory.setComment(FailCause.LD2);
                        case "LD3" -> ordersHistory.setComment(FailCause.LD3);
                        case "LD4" -> ordersHistory.setComment(FailCause.LD4);
                        default -> ordersHistory.setComment(FailCause.LD5);
                    }
                }
                case "DELIVERED" -> {
                    warehouse.setAvailable(warehouse.getAvailable() + 1);
                    orders.setOrderStatus(OrderStatus.DELIVERED);
                }
                default -> orders.setOrderStatus(OrderStatus.IN_STORED);
            }
            ordersHistory.setStatus(orders.getOrderStatus());
            warehouseService.save(warehouse);
            orderHistoryService.save(ordersHistory);
            return new ResponseEntity<>(ordersRepository.save(orders), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public Orders save(Orders orders) {
        return ordersRepository.save(orders);
    }

    @Override
    public Orders findById(String orderId) throws CustomException {
        Orders orders = ordersRepository.findById(orderId).orElse(null);
        if (orders == null) {
            throw new CustomException("SYSS-1100", HttpStatus.NOT_FOUND);
        }
        return orders;
    }

    @Override
    public List<Orders> findTop100ByOrderStatus(OrderStatus orderStatus) {
        return ordersRepository.findTop100ByOrderStatus(orderStatus);
    }

    @Override
    @Transactional
    public Orders createOrder(OrderRequest orderRequest, Users users) throws CustomException {
        Orders orders = Config.objectMapper.convertValue(orderRequest, Orders.class);
        orders.setCreateDate(Config.date());
        orders.setUsers(users);
        orders.setOrderCode(generateOrderCode(orders.getCreateDate()));
        orders.setOrderStatus(OrderStatus.NEW_ORDER);
        orders.setFailCount(0);
        Orders newOrder = save(orders);

        OrdersHistory ordersHistory = orderHistoryService.buildHistoryOrderHistory(orders, users, OrderStatus.NEW_ORDER);
        orderHistoryService.save(ordersHistory);
        return newOrder;
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> importExcelOrder(MultipartFile multipartFile, Users users) {
        try {
            Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, String> errors = new HashMap<>();
            List<OrderRequest> ordersList = new ArrayList<>();
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() < 2) {
                    continue;
                }
                if (row.getCell(row.getFirstCellNum()).getCellType().equals(CellType.BLANK)) {
                    break;
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                OrderRequest orders = new OrderRequest();

                while (cellIterator.hasNext()) {
                    //Read cell
                    Cell cell = cellIterator.next();
                    Object cellValueObject = getCellValue(cell);
                    if (cell.getColumnIndex() > ALPHABET.length) {
                        break;
                    }

                    String cellValue = "";
                    if (cellValueObject != null) {
                        cellValue = cellValueObject.toString();
                    }

                    // Set value for  object
                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex) {
                        case COLUMN_PROVIDER_NAME:
                            if (cellValue.isEmpty()){
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "tên không được để trống");
                                break;
                            }
                            if (!Validator.isValidName(cellValue)) {
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "tên từ 3 - 50 ký tự");
                                break;
                            }
                            orders.setProviderName(cellValue);
                            break;
                        case COLUMN_PROVIDER_PHONE:
                            if (cellValue.isEmpty()){
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "số điện thoại không được để trống");
                                break;
                            }
                            if (!Validator.isValidPhone(cellValue)) {
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "không dúng định dạnh số điện thoại việt nam");
                                break;
                            }
                            orders.setProviderPhone(cellValue);
                            break;
                        case COLUMN_PROVIDER_ADDRESS:
                            if (cellValue.isEmpty()){
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "địa chỉ không được để trống");
                                break;
                            }
                            Map<String, Double> providerCoordinate = Config.getCoordinates(cellValue);
                            if (providerCoordinate == null) {
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "không xác định được địa chỉ");
                                break;
                            }
                            orders.setProviderAddress(cellValue);
                            orders.setProviderLongitude(providerCoordinate.get("longitude"));
                            orders.setProviderLatitude(providerCoordinate.get("latitude"));
                            break;
                        case COLUMN_PROVIDER_EMAIL:
                            if (cellValue.isEmpty()){
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "email không được để trống");
                                break;
                            }
                            if (!Validator.isValidEmail(cellValue)) {
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "không đúng định dạng email");
                                break;
                            }
                            orders.setProviderEmail(cellValue);
                            break;
                        case COLUMN_RECEIVER_NAME:
                            if (cellValue.isEmpty()){
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "tên không được để trống");
                                break;
                            }
                            if (!Validator.isValidName(cellValue)) {
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "tên từ 3 - 50 ký tự");
                                break;
                            }
                            orders.setReceiverName(cellValue);
                            break;
                        case COLUMN_RECEIVER_PHONE:
                            if (cellValue.isEmpty()){
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "số điện thoại không được để trống");
                                break;
                            }
                            if (!Validator.isValidPhone(cellValue)) {
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "không dúng định dạnh số điện thoại việt nam");
                                break;
                            }
                            orders.setReceiverPhone(cellValue);
                            break;
                        case COLUMN_RECEIVER_ADDRESS:
                            if (cellValue.isEmpty()){
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "địa chỉ không được để trống");
                                break;
                            }
                            Map<String, Double> receiverCoordinate = Config.getCoordinates(cellValue);
                            if (receiverCoordinate == null) {
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "không xác định được địa chỉ");
                                break;
                            }
                            orders.setReceiverAddress(cellValue);
                            orders.setReceiverLongitude(receiverCoordinate.get("longitude"));
                            orders.setReceiverLatitude(receiverCoordinate.get("latitude"));
                            break;
                        case COLUMN_RECEIVER_EMAIL:
                            if (cellValue.isEmpty()){
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "email không được để trống");
                                break;
                            }
                            if (!Validator.isValidEmail(cellValue)) {
                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "không đúng định dạng email");

                                break;
                            }
                            orders.setReceiverEmail(cellValue);
                            break;
                        default:
                            break;
                    }
                }
                ordersList.add(orders);
            }

            if (!errors.isEmpty()) {
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            } else {
                for (OrderRequest request : ordersList) {
                    createOrder(request, users);
                }
                errors.put("SYSS-0001", "Success");
                return new ResponseEntity<>(errors, HttpStatus.OK);
            }
        } catch (IOException | CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteOrderAndHistory(String orderId) throws CustomException {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Đơn hàng không tồn tại", HttpStatus.NOT_FOUND));

        orderHistoryService.deleteOrderHistoryByOrder(order);

        ordersRepository.delete(order);
    }

    @Override
    public Page<Orders> getAllOrders(Pageable pageable, Users users) {
        return ordersRepository.findAllByUsers(pageable, users);
    }

    @Override
    public Page<Orders> searchOrders(String keyword, Date createDate, OrderStatus orderStatus, String warehouseCode, Users users, Pageable pageable) {
        java.sql.Date sqlDate = null;
        if(createDate != null){
            sqlDate = new java.sql.Date(createDate.getTime());
        }
        return ordersRepository.searchOrders(keyword, sqlDate, orderStatus, warehouseCode, users, pageable);
    }
    @Override
    @Transactional
    public Orders createSingleOrder(OrderRequest orders, Users users) throws CustomException {
        orders.setProviderPhone(users.getPhone());
        orders.setProviderName(users.getShopName());
        orders.setProviderAddress(users.getAddress());
        orders.setProviderEmail(users.getEmail());
        validateOrder(orders);
        return createOrder(orders, users);
    }

    @Override
    public Page<Orders> findOrdersByOrderStatusAndUser(Pageable pageable, OrderStatus status, Users users) {
        return ordersRepository.findByOrderStatusAndUsers(pageable, status, users);
    }


    @Override
    public OrdersResponse coordinateToWareHouse(Orders orders, Users users) {

        return null;
    }

    @Override
    @Transactional
    public void autoCoordinate(Orders orders, List<Warehouse> warehouses) throws IOException, InterruptedException, CustomException {
        Map.Entry<String, DistanceMatrixResponse.Result> shortestDistance = shortestDistance(getDistances(orders, warehouses));
        if (shortestDistance == null) {
            orders.setOrderStatus(OrderStatus.DELIVERY_FAIL);
            save(orders);
            throw new CustomException("Khong tinh duoc khoang cach tu kho den dia chi nguoi nhan don hang " + orders.getOrderCode(), HttpStatus.BAD_REQUEST);
        }
        Warehouse warehouse = warehouseService.findById(shortestDistance.getKey());
        Users users = userRepository.findByEmail("system");
        if (warehouse == null) {
            throw new CustomException("Khong tim thay kho ma " + shortestDistance.getKey(), HttpStatus.BAD_REQUEST);
        }
        OrdersHistory ordersHistory = orderHistoryService.buildHistoryOrderHistory(orders, users, OrderStatus.IN_STORED);
        warehouse.setAvailable(warehouse.getAvailable() - 1);
        orders.setOrderStatus(OrderStatus.IN_STORED);
        orders.setWarehouse(warehouse);
        save(orders);
        orderHistoryService.save(ordersHistory);
        warehouseService.save(warehouse);
    }

    @Override
    public Orders editOrder(String orderCode, OrderRequest orderRequest, Users users) throws CustomException {
        Orders orders = findById(orderCode);

        if (!users.getUserId().equals(orders.getUsers().getUserId())) {
            throw new CustomException(" ", HttpStatus.UNAUTHORIZED);
        }
        if (!orders.getOrderStatus().equals(OrderStatus.NEW_ORDER)) {
            throw new CustomException(" ", HttpStatus.NOT_ACCEPTABLE);
        }
        validateOrder(orderRequest);
        orders = objectMapper.convertValue(orderRequest, Orders.class);
        orders.setOrderCode(orderCode);
        orders.setCreateDate(date());
        orders.setUsers(users);
        orders.setOrderStatus(OrderStatus.NEW_ORDER);
        orders.setFailCount(0);

//        OrdersHistory ordersHistory = orderHistoryService.findByStatusAndOrders(OrderStatus.NEW_ORDER, orders);
//        ordersHistory.setUpdateAt(new Date());
//        orderHistoryService.save(ordersHistory);
        return ordersRepository.save(orders);
    }

    @Override
    @Transactional
    public Orders returnOrder(Orders orders, Users users) {
        orders.setOrderStatus(OrderStatus.RETURNED);
        OrdersHistory ordersHistory = orderHistoryService.buildHistoryOrderHistory(orders, users, OrderStatus.RETURNED);
        Warehouse warehouse = orders.getWarehouse();
        warehouse.setAvailable(warehouse.getAvailable() + 1);
        orderHistoryService.save(ordersHistory);
        warehouseService.save(warehouse);
        return ordersRepository.save(orders);
    }


    // private


    private void orderRestData(OrdersResponse response, List<OrdersHistory> ordersHistories) {
        int failCount = 0;
        for (OrdersHistory ordersHistory : ordersHistories) {
            switch (ordersHistory.getStatus()) {
                case DELIVERY_FAIL -> failCount += 1;
                case RETURNED -> response.setReturnDate(ordersHistory.getUpdateAt());
                case DELIVERED -> response.setDeliverDate(ordersHistory.getUpdateAt());
                case IN_STORED -> response.setStoreDate(ordersHistory.getUpdateAt());
            }
        }
        response.setFailCount(failCount);
    }

    private String generateOrderCode(Date dateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        String prefix = "DH-" + simpleDateFormat.format(dateTime);
        int maxCode = ordersRepository.findAllByOrderCodeContains(prefix).size();
        return String.format("%s-%05d", prefix, maxCode + 1);
    }

    private String destinations(List<Warehouse> warehouses) {

        StringBuilder destinations = new StringBuilder("&destinations=");
        for (Warehouse warehouse : warehouses) {
            destinations.append(warehouse.getLatitude()).append(",").append(warehouse.getLongitude()).append(";");
        }
        destinations.deleteCharAt(destinations.lastIndexOf(";"));
        return destinations.toString();
    }

    private String origins(Orders orders) {
        return "&origins=" + orders.getReceiverLatitude() + "," + orders.getReceiverLongitude();
    }

    private Map<String, DistanceMatrixResponse.Result> getDistances(Orders orders, List<Warehouse> warehouses) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(Config.DISTANCE_URL + origins(orders) + destinations(warehouses))).build();
        DistanceMatrixResponse response = distanceMatrixResponse(request);
        List<DistanceMatrixResponse.ResourceSet> resourceSets = response.getResourceSets();
        if (resourceSets.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, DistanceMatrixResponse.Result> resultDistance = resultDistance(warehouses, resourceSets.get(0).getResources().get(0).getResults());
        if (resultDistance.isEmpty()) {
            return new HashMap<>();
        }
        return resultDistance;
    }

    private DistanceMatrixResponse distanceMatrixResponse(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = Config.client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonResponse = response.body();
        return Config.objectMapper.readValue(jsonResponse, DistanceMatrixResponse.class);
    }

    private Map<String, DistanceMatrixResponse.Result> resultDistance(List<Warehouse> warehouses, List<DistanceMatrixResponse.Result> results) {
        Map<String, DistanceMatrixResponse.Result> resultMap = new HashMap<>();
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getTravelDistance() != -1) {
                resultMap.put(warehouses.get(i).getWarehouseCode(), results.get(i));
            }
        }
        return resultMap;
    }

    public Map.Entry<String, DistanceMatrixResponse.Result> shortestDistance(Map<String, DistanceMatrixResponse.Result> resultMap) {
        if (resultMap.isEmpty()) {
            return null;
        }
        Map.Entry<String, DistanceMatrixResponse.Result> result = null;
        double travelDistance = Double.MAX_VALUE;
        for (Map.Entry<String, DistanceMatrixResponse.Result> a : resultMap.entrySet()) {
            if (a.getValue().getTravelDistance() < travelDistance) {
                travelDistance = a.getValue().getTravelDistance();
                result = a;
            }
        }
        return result;
    }

    private Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        Object cellValue = null;
        switch (cellType) {
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = evaluator.evaluate(cell).getNumberValue();
                break;
            case NUMERIC:
                cellValue = cell.getNumericCellValue();
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return cellValue;
    }

    private Map<String, String> validateOrder(OrderRequest orderRequest) throws CustomException {
        Map<String, String> returnMap = new HashMap<>();
        Map<String, Double> providerCoordinate = Config.getCoordinates(orderRequest.getProviderAddress());
        if (providerCoordinate == null) {
            returnMap.put("providerAddress", "khong xac dinh duoc vi tri tren ban do");
        } else {
            setProviderCoordinate(orderRequest, providerCoordinate);
        }
        Map<String, Double> receiverCoordinate = Config.getCoordinates(orderRequest.getReceiverAddress());
        if (receiverCoordinate == null) {
            returnMap.put("receiverAddress", "khong xac dinh duoc vi tri tren ban do");
        } else {
            setReceiverCoordinate(orderRequest, receiverCoordinate);
        }
        if (!Validator.isValidName(orderRequest.getProviderName())) {
            returnMap.put("providerName", "khong hop le");
        }
        if (!Validator.isValidName(orderRequest.getReceiverName())) {
            returnMap.put("receiverName", "khong hop le");
        }
        if (!Validator.isValidEmail(orderRequest.getProviderEmail())) {
            returnMap.put("providerEmail", "khong hop le");
        }
        if (!Validator.isValidEmail(orderRequest.getReceiverEmail())) {
            returnMap.put("receiverEmail", "khong hop le");
        }
        if (!Validator.isValidPhone(orderRequest.getReceiverPhone())) {
            returnMap.put("receiverPhone", "khong hop le");
        }
        if (!Validator.isValidPhone(orderRequest.getProviderPhone())) {
            returnMap.put("providerPhone", "khong hop le");
        }
        if (!returnMap.isEmpty()) {
            throw new CustomException(returnMap.toString(), HttpStatus.BAD_REQUEST);
        }
        return returnMap;
    }
    private void setProviderCoordinate(OrderRequest orderRequest, Map<String, Double> coorMap) {
        orderRequest.setProviderLongitude(coorMap.get("longitude"));
        orderRequest.setProviderLatitude(coorMap.get("latitude"));
    }

    private void setReceiverCoordinate(OrderRequest orderRequest, Map<String, Double> coorMap) {
        orderRequest.setReceiverLongitude(coorMap.get("longitude"));
        orderRequest.setReceiverLatitude(coorMap.get("latitude"));
    }
}


