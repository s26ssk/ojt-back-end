package com.ra.service.impl;

import com.ra.dto.request.SelectDateRequest;
import com.ra.dto.response.*;
import com.ra.model.OrderStatus;
import com.ra.model.Users;
import com.ra.model.Warehouse;
import com.ra.repository.OrdersRepository;
import com.ra.repository.WareHouseRepository;
import com.ra.service.inter.IStatisticalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class StatisticalServiceImpl implements IStatisticalService {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    WareHouseRepository wareHouseRepository;
    @Override
    public List<DailyReportResponse> getDailyFromWarehouseCode(SelectDateRequest request) {
        Date fromDate = request.getFrom();
        Date toDate = request.getTo();
        long daysBetween = TimeUnit.DAYS.convert(toDate.getTime() - fromDate.getTime(), TimeUnit.MILLISECONDS);
        Warehouse warehouse = wareHouseRepository.findById(request.getWarehouseCode()).orElseThrow();
        List<DailyReportResponse> dailyReportResponses = new ArrayList<>();
        List<String> predefinedReasons = Arrays.asList("Khách hàng vắng, hẹn giao sau", "Không liên hệ được với khách hàng", "Từ chối nhận vì hàng không như mô tả", "Từ chối nhận vì kiện hàng rách/móp", "Lý do khác");
        for (long i = 0; i < daysBetween; i++) {
            Date currentDate = new Date(fromDate.getTime() + TimeUnit.DAYS.toMillis(i));
            List<Reason> reasons = ordersRepository.getReasonByDate(currentDate, request.getWarehouseCode());
            for (String predefinedReason : predefinedReasons) {
                boolean reasonExists = false;
                for (Reason existingReason : reasons) {
                    if (existingReason.getReason().equals(predefinedReason)) {
                        reasonExists = true;
                        break;
                    }
                }
                if (!reasonExists) {
                    reasons.add(new Reason(predefinedReason, 0L));
                }
            }
            Long success = ordersRepository.countByOrderStatusAndCreateDate(OrderStatus.DELIVERED, currentDate, request.getWarehouseCode());
            Long fail = ordersRepository.countByOrderStatusAndCreateDate(OrderStatus.RETURNED, currentDate,request.getWarehouseCode());
            reasons.add(new Reason("Đơn giao thành công", success));
            reasons.add(new Reason("Đơn giao thất bại", fail));
            DailyReportResponse dailyReportResponse = DailyReportResponse.builder().date(currentDate).warehouseCode(warehouse.getWarehouseCode()).warehouseName(warehouse.getWarehouseName()).reasons(reasons).build();
            dailyReportResponses.add(dailyReportResponse);
        }
        return dailyReportResponses;
    }

    @Override
    public List<StatisticalOrderNumberResponse> getStatistical(SelectDateRequest request) {
        List<StatisticalOrderNumberResponse> statisticalOrderNumberResponses = new ArrayList<>();
        Date fromDate = request.getFrom();
        Date toDate = request.getTo();
        long daysBetween = TimeUnit.DAYS.convert(toDate.getTime() - fromDate.getTime(), TimeUnit.MILLISECONDS);
        List<Warehouse> warehouses = wareHouseRepository.findAll();
        for (long i = 0; i < daysBetween; i++) {
            Date currentDate = new Date(fromDate.getTime() + TimeUnit.DAYS.toMillis(i));
            List<WarehouseOrderTotal> warehouseOrderTotals = new ArrayList<>();
            Set<String> existingWarehouseCodes = new HashSet<>();
            for (Warehouse warehouse : warehouses) {
                List<WarehouseOrderTotal> warehouseOrderTotal = ordersRepository.getWarehouseOrderTotal(currentDate, warehouse.getWarehouseCode());
                if (!warehouseOrderTotal.isEmpty()) {
                    warehouseOrderTotals.addAll(warehouseOrderTotal);
                    existingWarehouseCodes.add(warehouse.getWarehouseCode());
                } else if (!existingWarehouseCodes.contains(warehouse.getWarehouseCode())) {
                    warehouseOrderTotals.add(WarehouseOrderTotal.builder().warehouseCode(warehouse.getWarehouseCode()).warehouseName(warehouse.getWarehouseName()).totalOrder(0L).build());
                    existingWarehouseCodes.add(warehouse.getWarehouseCode());
                }
            }
            if (!warehouseOrderTotals.isEmpty()) {
                StatisticalOrderNumberResponse statisticalOrderNumberResponse = StatisticalOrderNumberResponse.builder().date(currentDate).warehouseOrderTotals(warehouseOrderTotals).build();
                statisticalOrderNumberResponses.add(statisticalOrderNumberResponse);
            }
        }

        return statisticalOrderNumberResponses;
    }


    @Override
    public List<StatisticalResponse> getStatisticalByUser(SelectDateRequest request, Users user) {
        return ordersRepository.getStatisticalByUser(request.getFrom(), request.getTo(), user);
    }
}
