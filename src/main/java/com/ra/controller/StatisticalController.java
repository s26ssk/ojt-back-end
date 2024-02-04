package com.ra.controller;

import com.ra.dto.request.SelectDateRequest;
import com.ra.dto.response.DailyReportResponse;
import com.ra.dto.response.StatisticalOrderNumberResponse;
import com.ra.dto.response.StatisticalResponse;
import com.ra.model.Warehouse;
import com.ra.security.principal.UserPrinciple;
import com.ra.service.inter.IStatisticalService;
import com.ra.service.inter.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class StatisticalController {

    @Autowired
    IStatisticalService statisticalService;
    @Autowired
    WarehouseService warehouseService;


    @PostMapping("/daily-warehouse")
    public ResponseEntity<List<DailyReportResponse>> getDailyFromWarehouseCode(@RequestBody SelectDateRequest request)  {
        return new ResponseEntity<>(statisticalService.getDailyFromWarehouseCode(request), HttpStatus.OK);
    }

    @PostMapping("/statistical")
    public ResponseEntity<List<StatisticalOrderNumberResponse>> getStatisticalWarehouses(@RequestBody SelectDateRequest request)  {
        return new ResponseEntity<>(statisticalService.getStatistical(request), HttpStatus.OK);
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<Warehouse>> getWarehouses() {
        return new ResponseEntity<>(warehouseService.getAll(), HttpStatus.OK);
    }
    @PostMapping ("/order-statistical")
    public ResponseEntity<List<StatisticalResponse>> getStatisticalByUser(@RequestBody SelectDateRequest request)  {
        return new ResponseEntity<>(statisticalService.getStatisticalByUser(request, userPrinciple().getUser()), HttpStatus.OK);
    }

    private UserPrinciple userPrinciple() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrinciple) authentication.getPrincipal();
    }

}
