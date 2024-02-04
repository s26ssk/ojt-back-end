package com.ra.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public  class WarehouseOrderTotal {
    private String warehouseCode;
    private String warehouseName;
    private Long totalOrder;
}