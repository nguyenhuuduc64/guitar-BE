package com.service;

import java.util.List;
import java.util.UUID;

import com.entity.Role;
import com.exception.AppException;
import com.exception.ErrorCode;
import com.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dto.request.UserCreationRequest;
import com.dto.request.UserUpdateRequest;
import com.dto.response.UserResponse;
import com.entity.User;
import com.mapper.UserMapper;
import com.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Slf4j
@Service
@RequiredArgsConstructor // bien nao co defind la final thi se duoc inject vao class
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true) // cac field khong co type thi se mac dinh la
                                                                     // private va dua vao contructor nhu final

public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository  roleRepository;
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("User existed");

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));


        Role finalRole;

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            finalRole = roleRepository.findById(request.getRoles())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRoles()));
        } else {
            finalRole = roleRepository.findById("user") // Dùng nháy kép thưa ông chủ!
                    .orElseThrow(() -> new RuntimeException("Default Role 'user' not found in DB"));
        }

        user.setRoles(finalRole);
        user = userRepository.save(user);





        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        //SecurityContextHolder luu thong tin dang nhap cua user sau khi login
        var context = SecurityContextHolder.getContext();
        //lay username tu info cua nguoi dung dang dang nhap hien tai
        String name = context.getAuthentication().getName();

        User byUsername = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return  userMapper.toUserResponse(byUsername);
    }

    //kiểm tra trước khi method được thực hiện
    //dung hasRole thi scope la ROLE_ADMIN con hasAuthority khi co SCOPE_ADMIN
    //@PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("getUsers is called");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    // tra ve User Entity cua DB
    private User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    //thực hiện method trước khi kiểm tra
    //dieu kien kiem tra chinh chu moi co the thuc hien
    //returnObject la thong tin cua user lay tu Id
    //uathenticate la thong tin cua nguoi dung nhap hien tai qua jwt
    @PostAuthorize("returnObject.username == authentication.name || hasAuthority('SCOPE_admin')")
    public UserResponse getUserById(UUID id) {
        User user = getUserEntityById(id);
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(UUID id, UserUpdateRequest userUpdate) {
        User user = getUserEntityById(id);
        userMapper.updateUser(user, userUpdate);

        if (userUpdate.getRoles() != null && !userUpdate.getRoles().isEmpty()) {
            // 1. Lấy ra String tên role từ Set gửi lên
            String roleName = userUpdate.getRoles().iterator().next();

            // 2. Tìm đối tượng Role thực sự trong DB
            Role role = roleRepository.findById(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

            // 3. Gán TRỰC TIẾP đối tượng role vào user (Vì Entity là Single Object)
            // KHÔNG dùng Set.of() ở đây nữa thưa ông chủ!
            user.setRoles(role);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
