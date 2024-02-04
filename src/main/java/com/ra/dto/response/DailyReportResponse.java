package com.ra.dto.response;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyReportResponse {
    private Date date;
    private String warehouseCode;
    private String warehouseName;
    private List<Reason> reasons;
}
