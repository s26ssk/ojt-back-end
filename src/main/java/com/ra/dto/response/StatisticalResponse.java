package com.ra.dto.response;

import com.ra.model.OrderStatus;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StatisticalResponse {
    private Long inStore;
    private Long delivered;
    private Long deliveryFail;
    private Long returned;
}

