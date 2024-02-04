package com.ra.repository;

import com.ra.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseRepository extends JpaRepository<Warehouse, String> {
    @Query("SELECT w FROM Warehouse w WHERE w.available > 0")
    List<Warehouse> availableWareHouse();
}
