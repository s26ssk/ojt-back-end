package com.ra.repository;

import com.ra.dto.response.OrdersResponse;
import com.ra.dto.response.Reason;
import com.ra.dto.response.StatisticalResponse;
import com.ra.dto.response.WarehouseOrderTotal;
import com.ra.model.OrderStatus;
import com.ra.model.Orders;
import com.ra.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, String> {
    List<Orders> findAllByOrderCodeContains(String prefix);

    List<Orders> findTop100ByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT o FROM Orders o " +
            "WHERE (:keyword IS NULL OR " +
            "       o.orderCode LIKE %:keyword% OR " +
            "       o.providerPhone LIKE %:keyword% OR " +
            "       o.receiverPhone LIKE %:keyword% OR " +
            "       o.providerName LIKE %:keyword% OR " +
            "       o.receiverName LIKE %:keyword%) " +
            "AND (:createDate IS NULL OR " +
            "       YEAR(o.createDate) = YEAR(:createDate) AND " +
            "       MONTH(o.createDate) = MONTH(:createDate) AND " +
            "       DAY(o.createDate) = DAY(:createDate)) " +
            "AND (:orderStatus IS NULL OR o.orderStatus = :orderStatus) " +
            "AND (:warehouseCode IS NULL OR o.warehouse.warehouseCode = :warehouseCode)" +
            "AND(o.users = :user)")
    Page<Orders> searchOrders(@Param("keyword") String keyword,
                              @Param("createDate") Date createDate,
                              @Param("orderStatus") OrderStatus orderStatus,
                              @Param("warehouseCode") String warehouseCode,
                              @Param("user") Users user, Pageable pageable);

    Page<Orders> findAllByUsers(Pageable pageable, Users users);

    Page<Orders> findByOrderStatusAndUsers(Pageable pageable, OrderStatus orderStatus, Users users);

    @Query("SELECT NEW com.ra.dto.response.StatisticalResponse(" +
            "count((case  when o.orderStatus = 1 then 1 end )), " +
            "count((case  when o.orderStatus = 2 then 1 end )), " +
            "count((case  when o.orderStatus = 3 then 1 end )), " +
            "count((case  when o.orderStatus = 4 then 1 end )) " +
            ") from Orders o where cast(o.createDate as DATE ) between :from and :to and o.users = :user ")
    List<StatisticalResponse> getStatisticalByUser(@Param("from") Date from, @Param("to") Date to, @Param("user") Users user);

    @Query("select new com.ra.dto.response.WarehouseOrderTotal(o.warehouse.warehouseCode, o.warehouse.warehouseName, count(*)) from Orders o where cast(o.createDate as DATE ) = :date and o.warehouse.warehouseCode = :warehouseCode  group by o.warehouse.warehouseCode")
    List<WarehouseOrderTotal> getWarehouseOrderTotal(@Param("date") Date date, @Param("warehouseCode") String warehouseCode);

    @Query("select new com.ra.dto.response.Reason(oh.comment, count(*)) from Orders o join OrdersHistory oh on o.orderCode = oh.orderCode where cast(o.createDate as DATE) = :date and oh.comment is not null and o.warehouse.warehouseCode = :warehouseCode group by o.createDate, oh.comment")
    List<Reason> getReasonByDate(@Param("date") Date date, @Param("warehouseCode") String warehouseCode);

    @Query("select count(*) from Orders o where cast(o.createDate as DATE ) = :date and o.orderStatus = :orderStatus and o.warehouse.warehouseCode = :warehouseCode")
    Long countByOrderStatusAndCreateDate(@Param("orderStatus") OrderStatus orderStatus, @Param("date") Date date, @Param("warehouseCode") String warehouseCode);
}
