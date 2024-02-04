package com.ra.dto.response;

import com.ra.model.OrderStatus;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdersResponse {
    private String orderCode;
    private OrderStatus orderStatus;
    private String providerName;
    private String providerPhone;
    private String providerAddress;
    private String providerEmail;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String receiverEmail;
    private Date createDate;
    private Date storeDate;
    private Date deliverDate;
    private Date returnDate;
    private Integer failCount;
}
