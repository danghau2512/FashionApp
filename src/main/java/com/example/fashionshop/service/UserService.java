package com.example.fashionshop.service;

import com.example.fashionshop.dto.UserResponse;
import com.example.fashionshop.dto.UserSyncRequest;
import com.example.fashionshop.dto.UserUpdateRequest;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse syncUser(UserSyncRequest request) {
        User user = userRepository.findByFirebaseUid(request.getFirebaseUid())
                .orElse(null);

        if (user == null) {
            user = new User();
            user.setFirebaseUid(request.getFirebaseUid());
            user.setEmail(request.getEmail());
            user.setRole("CUSTOMER");
            user.setStatus("ACTIVE");
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