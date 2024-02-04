package com.ra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Warehouse {
    @Id
    private String warehouseCode;
    private String warehouseName;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer capacity;
    private Integer available;
}
