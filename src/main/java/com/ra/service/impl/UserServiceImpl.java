package com.ra.service.impl;

import com.ra.config.Validator;
import com.ra.dto.request.ChangePassRequest;
import com.ra.dto.request.UserLoginRequest;
import com.ra.dto.request.UserRegisterRequest;
import com.ra.dto.request.UserUpdateRequest;
import com.ra.dto.response.JwtUserResponse;
import com.ra.security.jwt.JWTEntryPoint;
import com.ra.service.inter.EmailService;
import com.ra.util.exception.CustomException;
import com.ra.model.Role;
import com.ra.model.Users;
import com.ra.repository.RoleRepository;
import com.ra.repository.UserRepository;

import com.ra.security.jwt.JWTProvider;
import com.ra.security.principal.UserPrinciple;
import com.ra.service.inter.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private EmailService emailService;

    private final Logger logger = LoggerFactory.getLogger(JWTEntryPoint.class);
    @Value("${uploadPath}")
    private String uploadPath;

    private UserPrinciple userPrinciple() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrinciple) authentication.getPrincipal();
    }
    @Override
    public ResponseEntity<Map<String,String>> handleRegister(UserRegisterRequest registerRequest)  {
        Users users;
        Map<String, String> returnMap;
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("ROLE_USER").orElse(null));

        users = Users.builder()
                .shopName(registerRequest.getShopName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .address(registerRequest.getAddress())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .failedLoginAttempts(0)
                .lastFailedLoginTime(Instant.now())
                .status(true)
                .roles(roles)
                .build();
        returnMap = userRegisterValidate(users);

        if (!returnMap.isEmpty()) {
            return new ResponseEntity<>(returnMap, HttpStatus.BAD_REQUEST);
        }
        userRepository.save(users);
        return new ResponseEntity<>(returnMap, HttpStatus.OK);
    }


    public JwtUserResponse handleLogin(UserLoginRequest userLoginRequestDTO) throws CustomException {
        Authentication authentication;
        Users user = userRepository.findByEmailOrPhone(userLoginRequestDTO.getEmailOrPhone());
        if (user == null) {
            throw new CustomException("Tài khoản hoặc mật khẩu sai", HttpStatus.BAD_REQUEST.value());
        }
        if (!user.getStatus()) {
            throw new CustomException("Tài khoản của bạn đã bị khóa", HttpStatus.BAD_REQUEST.value());
        }

        try {

            authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequestDTO.getEmailOrPhone(), userLoginRequestDTO.getPassword()));
            // Xử lý đăng nhập thành công
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            String token = jwtProvider.generateToken(userPrinciple);
            JwtUserResponse jwtUserResponse = JwtUserResponse.builder()
                    .shopName(userPrinciple.getUser().getShopName())
                    .address(userPrinciple.getUser().getAddress())
                    .email(userPrinciple.getUser().getEmail())
                    .phone(userPrinciple.getUser().getPhone())
                    .token(token)
                    .roles(userPrinciple.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toSet()))
                    .build();
            user.setFailedLoginAttempts(0);
            return jwtUserResponse;

        } catch (AuthenticationException e) {


            if (user.getFailedLoginAttempts() <= 3) {
                long currentTime = System.currentTimeMillis();
                Instant instant = Instant.ofEpochMilli(currentTime);

                // Tính toán khoảng thời gian
                Instant lastFailedLoginInstant = user.getLastFailedLoginTime().atZone(ZoneId.systemDefault()).toInstant();
                Duration duration = Duration.between(lastFailedLoginInstant, instant);
                // Chuyển đổi khoảng thời gian sang giây
                long timeDifferenceInSeconds = duration.getSeconds();

                if (timeDifferenceInSeconds <= 5 * 60) {
                    user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

                } else {
                    user.setFailedLoginAttempts(1);
                }
                user.setLastFailedLoginTime(instant);
            } else {
                throw new CustomException("Tài khoản của bạn đã bị khóa", HttpStatus.BAD_REQUEST.value());
            }

            throw new CustomException("Tài khoản hoặc mật khẩu không đúng", HttpStatus.BAD_REQUEST.value());

        } finally {


            if (user.getFailedLoginAttempts() >= 3) {
                if (user.getStatus()) {
                    emailService.sendMailtoUserBlooked(user);
                }

                user.setStatus(false); // Khóa tài

            }
            userRepository.save(user);


        }
    }
    @Override
    public ResponseEntity<Map<String, String>> changePassword(UserPrinciple userPrinciple, ChangePassRequest changePassRequest) {
        Map<String, String> errMap = new HashMap<>();
        if (!passwordEncoder.matches(changePassRequest.getCurPass(), userPrinciple.getPassword())) errMap.put("curPass", "không khớp với mật khẩu cũ");
        if (Validator.isStrongPassword(changePassRequest.getNewPass())) {
            if (!changePassRequest.getNewPass().equals(changePassRequest.getCfPass()))  errMap.put("confirm password", " không khớp với password");
        } else {
            errMap.put("password", "SYSS-0005");
        }
        if (!errMap.isEmpty()) {
            return new ResponseEntity<>(errMap, HttpStatus.BAD_REQUEST);
        } else {
            userRepository.updatePasswordByUserId(passwordEncoder.encode( changePassRequest.getNewPass()), userPrinciple.getUser().getUserId());
        }
        return new ResponseEntity<>(errMap, HttpStatus.OK);
    }
    private Map<String, String> userRegisterValidate(Users users) {
        Map<String, String> errMap = new HashMap<>();
        if (Validator.isValidEmail(users.getEmail())) {
            if (userRepository.existsByEmail(users.getEmail())) {
                errMap.put("email", "da ton tai");
            }
        } else {
            errMap.put("email", "SYSS-0005");
        }
        if (Validator.isValidPhone(users.getPhone())) {
            if (userRepository.existsByPhone(users.getPhone())) {
                errMap.put("phone", "da ton tai");
            }
        } else {
            errMap.put("phone", "SYSS-0005");
        }
        if (!Validator.isStrongPassword(users.getPassword())) {
            errMap.put("password", "SYSS-0005");
        }
        if (!Validator.isValidAddress(users.getAddress())) {
            errMap.put("address", "SYSS-0005");
        }
        if (!Validator.isValidName(users.getShopName())) {
            errMap.put("shopName", "SYSS-0005");
        }
        return errMap;
    }
    private Map<String, String> userUpdateValidate(Users users) {
        Map<String, String> errMap = new HashMap<>();
        if (!Validator.isValidEmail(users.getEmail())) {
            errMap.put("email", "SYSS-0005");
        }
        if (!Validator.isValidPhone(users.getPhone())) {
            errMap.put("phone", "SYSS-0005");
        }
        if (!Validator.isStrongPassword(users.getPassword())) {
            errMap.put("password", "SYSS-0005");
        }
        if (!Validator.isValidAddress(users.getAddress())) {
            errMap.put("address", "SYSS-0005");
        }
        if (!Validator.isValidName(users.getShopName())) {
            errMap.put("shopName", "SYSS-0005");
        }
        return errMap;
    }



    public String uploadAvatar(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            File uploadFolder = new File(uploadPath);

            if (!uploadFolder.exists()) {
                if (uploadFolder.mkdirs()) {
                    logger.info("Created Upload Folder successfully");
                } else {
                    logger.error("Failed to create Upload Folder");
                }
            }
            String fullPath = uploadPath + fileName;
            FileCopyUtils.copy(file.getBytes(), new File(fullPath));

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ResponseEntity<Map<String,String>> updateProfile(UserPrinciple userPrinciple, UserUpdateRequest userUpdateRequest ) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("ROLE_USER").orElse(null));

        Users users = Users.builder()
                .userId(userPrinciple.getUser().getUserId())
                .shopName(userUpdateRequest.getShopName())
                .email(userPrinciple.getUser().getEmail())
                .phone(userPrinciple.getUser().getPhone())
                .address(userPrinciple.getUser().getAddress())
                .password(userPrinciple.getUser().getPassword())
                .roles(userPrinciple.getUser().getRoles())
                .failedLoginAttempts(0)
                .lastFailedLoginTime(Instant.now())
                .status(true)
                .build();
        Map<String, String> returnMap = userUpdateValidate(users);

//        if (file != null && !file.isEmpty()) {
//            String avatarFileName = uploadAvatar(file);
//            users.setAvatar(avatarFileName);
//        }

        Map<String, String> validationErrors = userUpdateValidate(users);
        if (!validationErrors.isEmpty()) {
            return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
        }

        userRepository.save(users);
        return new ResponseEntity<>(returnMap, HttpStatus.OK);
    }
}
