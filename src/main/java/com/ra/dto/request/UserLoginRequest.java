package com.ra.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserLoginRequest {
    private String emailOrPhone;
    private String password;
}

