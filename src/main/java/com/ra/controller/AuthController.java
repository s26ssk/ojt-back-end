package com.ra.controller;

import com.ra.dto.request.UserRegisterRequest;
import com.ra.dto.request.UserLoginRequest;
import com.ra.dto.response.JwtUserResponse;
import com.ra.service.impl.OrderServiceImpl;
import com.ra.service.inter.UserService;
import com.ra.util.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("/*")
@Slf4j
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> handleRegister(@RequestBody UserRegisterRequest userRegisterRequestDTO){

        return userService.handleRegister(userRegisterRequestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtUserResponse> handleLogin(@RequestBody UserLoginRequest users) throws CustomException {
        return new ResponseEntity<>(userService.handleLogin(users), HttpStatus.CREATED);
    }
}
