package com.ra.dto.request;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserUpdateRequest {
    private String shopName;
    private String avatar;
    private String address;

}
