package com.ra.dto.response;

import com.ra.model.OrderStatus;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private String orderCode;
    private Date createDate;
    private Integer quantity;
    private OrderStatus orderStatus;
    private String providerName;
    private String providerPhone;
    private String providerAddress;
    private String providerEmail;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String receiverEmail;
    private List<OrderHistoryResponse> ordersHistories;
}
