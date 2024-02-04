package com.ra.service.inter;

import com.ra.dto.request.ChangePassRequest;
import com.ra.dto.request.UserLoginRequest;
import com.ra.dto.request.UserRegisterRequest;
import com.ra.dto.request.UserUpdateRequest;
import com.ra.dto.response.JwtUserResponse;


import com.ra.security.principal.UserPrinciple;
import com.ra.util.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    ResponseEntity<Map<String,String>> handleRegister(UserRegisterRequest registerRequest) ;

    JwtUserResponse handleLogin(UserLoginRequest users) throws CustomException;
    ResponseEntity<Map<String, String>> changePassword(UserPrinciple userPrinciple, ChangePassRequest changePassRequest);

    String uploadAvatar(MultipartFile file);

    ResponseEntity<Map<String,String>> updateProfile(UserPrinciple userPrinciple, UserUpdateRequest userUpdateRequest);

}
