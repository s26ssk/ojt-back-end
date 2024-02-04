package com.ra.service.impl;
import com.ra.config.Config;
import com.ra.model.OrderStatus;
import com.ra.model.Orders;
import com.ra.model.OrdersHistory;
import com.ra.service.inter.OrderHistoryService;
import com.ra.service.inter.OrderService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.*;

@Service
public class LabelService {
    @Autowired
    private OrderService orderService;

    public byte[] generateLabel(String orderCode) {
        try {
            Orders orders = orderService.findById(orderCode);
            if (orders.getOrderStatus() != OrderStatus.NEW_ORDER) {
                // Load file mẫu nhãn
                InputStream template = getClass().getResourceAsStream("/label/Order-Label1.jrxml");
                if (template == null) {
                    throw new IllegalArgumentException("Template not found");
                }
                // Compile mẫu nhãn
                JasperReport jasperReport = JasperCompileManager.compileReport(template);

                // Tạo bản đồ dữ liệu từ thông tin đơn hàng
                Map<String, Object> parameter = new HashMap<>();
                parameter.put("OrderCode", orders.getOrderCode());
                parameter.put("providerName", orders.getProviderName());
                parameter.put("providerAddress", orders.getProviderAddress());
                parameter.put("providerPhone", orders.getProviderPhone());
                parameter.put("receiverName", orders.getReceiverName());
                parameter.put("receiverAddress", orders.getReceiverAddress());
                parameter.put("receiverPhone", orders.getReceiverPhone());
                parameter.put("createTime", orders.getCreateDate());
                parameter.put("barcode", Config.generateBarcodeImage(orders.getOrderCode()));
                parameter.put("qrCode", Config.generateQRCodeImage(orders.getOrderCode()));
                parameter.put("warehouseName", orders.getWarehouse().getWarehouseName());
                parameter.put("warehouseCode", orders.getWarehouse().getWarehouseCode());
                // Tạo bảng dữ liệu từ Map
                JRDataSource dataSource = new JRBeanArrayDataSource(new Map[]{parameter});
                // Tạo JasperPrint từ mẫu và dữ liệu
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, dataSource);
                // Xuất ra dạng byte array (PDF)
                return JasperExportManager.exportReportToPdf(jasperPrint);
            }else {
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

