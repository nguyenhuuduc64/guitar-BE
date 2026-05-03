package com.controller;

import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.request.ApiResponse;
import com.dto.request.UserCreationRequest;
import com.dto.request.UserUpdateRequest;
import com.dto.response.UserResponse;
import com.entity.User;
import com.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    private final UserService userService;
    @PostMapping("")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("success");
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping("")
    ApiResponse<Object> getUsers(){
        log.info("get users");
        return ApiResponse.builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<Object> getMyInfo(){
        return ApiResponse.builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") UUID userId){
        return ApiResponse.<UserResponse>builder()
                .message("get user successfully")
                .result(userService.getUserById(userId))
                .code(200)
                .build();
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@RequestBody UserUpdateRequest userUpdate, @PathVariable("userId") UUID userId){
        log.info("user update{}", userUpdate);
        return userService.updateUser(userId, userUpdate);
    }
    @DeleteMapping("/{userId}")
    String  deleteUser(@PathVariable("userId") UUID userId){
        userService.deleteUser(userId); 
        return "user has been deleted";
    }

}
