package com.ra.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegisterRequest {
    private String shopName;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String avatar;
    private String confirmPassword;
    private Set<String> roles;
}
