package com.ra.dto.request;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChangePassRequest {
    private String curPass;
    private  String newPass;
    private  String cfPass;
}

