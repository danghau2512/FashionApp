package com.example.fashionshop.service;

import com.example.fashionshop.dto.UserResponse;
import com.example.fashionshop.dto.UserSyncRequest;
import com.example.fashionshop.dto.UserUpdateRequest;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_LOCKED = "LOCKED";
    private static final String ROLE_CUSTOMER = "CUSTOMER";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse syncUser(UserSyncRequest request) {
        User user = userRepository.findByFirebaseUid(request.getFirebaseUid())
                .orElse(null);

        String normalizedEmail = normalizeEmail(request.getEmail());

        if (user == null && normalizedEmail != null) {
            user = userRepository.findByEmail(normalizedEmail)
                    .orElse(null);

            if (user != null) {
                user.setFirebaseUid(request.getFirebaseUid());
            }
        }

        if (user == null) {
            user = new User();

            user.setFirebaseUid(request.getFirebaseUid());
            user.setEmail(normalizedEmail);
            user.setFullName(request.getFullName());
            user.setPhone(request.getPhone());
            user.setAvatarUrl(request.getAvatarUrl());
            user.setRole(ROLE_CUSTOMER);
            user.setStatus(STATUS_ACTIVE);
        } else {
            if (STATUS_LOCKED.equalsIgnoreCase(user.getStatus())) {
                throw new RuntimeException("Tài khoản đã bị khóa");
            }

            if (user.getFirebaseUid() == null || user.getFirebaseUid().trim().isEmpty()) {
                user.setFirebaseUid(request.getFirebaseUid());
            }

            if (normalizedEmail != null) {
                user.setEmail(normalizedEmail);
            }

            if ((user.getFullName() == null || user.getFullName().trim().isEmpty())
                    && isNotBlank(request.getFullName())) {
                user.setFullName(request.getFullName());
            }

            if (isNotBlank(request.getPhone())) {
                user.setPhone(request.getPhone());
            }

            if (isNotBlank(request.getAvatarUrl())) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
        }

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

        if (STATUS_LOCKED.equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(request.getAvatarUrl());

        User savedUser = userRepository.save(user);

        return UserResponse.fromEntity(savedUser);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}