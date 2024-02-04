package com.ra.controller;

import com.ra.dto.request.ChangePassRequest;
import com.ra.dto.request.UserRegisterRequest;
import com.ra.dto.request.UserUpdateRequest;
import com.ra.security.principal.UserPrinciple;
import com.ra.service.inter.UserService;
import com.ra.util.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @PutMapping("/user/changePassword")
    public ResponseEntity<Map<String, String>> changePassWord(@RequestBody ChangePassRequest changePassRequest) {
        return userService.changePassword(userPrinciple(), changePassRequest);
    }

    @PutMapping("/user/update-profile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateProfile(userPrinciple(), userUpdateRequest);
    }
    private UserPrinciple userPrinciple() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrinciple) authentication.getPrincipal();
    }
}
