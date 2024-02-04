package com.ra.service.inter;

import com.ra.model.Warehouse;

import java.util.List;

public interface WarehouseService {
    List<Warehouse> getAll();
    Warehouse findById(String id);
    Warehouse save(Warehouse warehouse);
}
