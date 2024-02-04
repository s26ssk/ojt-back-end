package com.ra.dto.response;

import lombok.*;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtUserResponse {
    private String shopName;
    private String phone;
    private String email;
    private String token;
    private String address;
    private Set<String> roles;
}
