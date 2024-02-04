package com.ra.dto.response;

import lombok.*;

import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StatisticalOrderNumberResponse {

    private Date date;
    private List<WarehouseOrderTotal> warehouseOrderTotals;

}

