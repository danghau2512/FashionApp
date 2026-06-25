package com.example.fashionshop.service;

import com.example.fashionshop.dto.UserResponse;
import com.example.fashionshop.dto.UserSyncRequest;
import com.example.fashionshop.dto.UserUpdateRequest;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String STATUS_LOCKED = "LOCKED";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse syncUser(UserSyncRequest request) {
        User user = userRepository.findByFirebaseUid(request.getFirebaseUid())
                .orElse(null);

        if (user == null && request.getEmail() != null) {
            user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                    .orElse(null);

            if (user != null) {
                user.setFirebaseUid(request.getFirebaseUid());
            }
        }

        if (user == null) {
            user = new User();
            user.setFirebaseUid(request.getFirebaseUid());
            user.setEmail(request.getEmail() == null ? null : request.getEmail().trim().toLowerCase());
            user.setRole("CUSTOMER");
            user.setStatus("ACTIVE");
        }

        if (STATUS_LOCKED.equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(request.getAvatarUrl());

        User savedUser = userRepository.save(user);

        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserByFirebaseUid(String firebaseUid) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return UserResponse.fromEntity(user);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(request.getAvatarUrl());

        User savedUser = userRepository.save(user);

        return UserResponse.fromEntity(savedUser);
    }
}
