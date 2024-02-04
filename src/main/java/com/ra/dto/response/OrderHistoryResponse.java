package com.ra.dto.response;

import com.ra.model.OrderStatus;
import com.ra.model.Orders;
import com.ra.model.Users;
import com.ra.model.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryResponse {
    private Users user;
    private Warehouse warehouse;
    private String orderCode;
    private OrderStatus status;
    private Date updateAt;
    private String comment;
}
