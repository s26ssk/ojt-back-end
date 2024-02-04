package com.ra.dto.request;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderRequest {
    private String providerName;
    private String providerPhone;
    private String providerAddress;
    private String providerEmail;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String receiverEmail;

    private Double providerLatitude;
    private Double providerLongitude;
    private Double receiverLatitude;
    private Double receiverLongitude;

}