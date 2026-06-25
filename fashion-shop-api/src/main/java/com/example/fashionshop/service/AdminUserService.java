package com.example.fashionshop.service;

import com.example.fashionshop.dto.AdminUserRequest;
import com.example.fashionshop.dto.AdminUserResponse;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_CUSTOMER = "CUSTOMER";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_LOCKED = "LOCKED";
    private static final String ADMIN_CREATED_UID_PREFIX = "ADMIN_CREATED_";

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AdminUserResponse> getUsers(String keyword, String role, String status) {
        String normalizedKeyword = normalize(keyword);
        String normalizedRole = normalize(role);
        String normalizedStatus = normalize(status);

        List<User> users = userRepository.searchAdminUsers(normalizedKeyword, normalizedRole, normalizedStatus);
        return users.stream()
                .map(AdminUserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public AdminUserResponse getUserById(Long id) {
        User user = getUserOrThrow(id);
        return AdminUserResponse.fromEntity(user);
    }

    @Transactional
    public AdminUserResponse createUser(AdminUserRequest request, Long adminId) {
        validateAdmin(adminId);

        String email = normalizeRequired(request.getEmail(), "Email không được để trống").toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        user.setFirebaseUid(ADMIN_CREATED_UID_PREFIX + UUID.randomUUID());
        user.setEmail(email);
        applyEditableFields(user, request);

        User savedUser = userRepository.save(user);
        return AdminUserResponse.fromEntity(savedUser);
    }

    @Transactional
    public AdminUserResponse updateUser(Long id, AdminUserRequest request, Long adminId) {
        validateAdmin(adminId);

        User user = getUserOrThrow(id);
        String email = normalizeRequired(request.getEmail(), "Email không được để trống").toLowerCase();

        userRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác");
            }
        });

        user.setEmail(email);
        applyEditableFields(user, request);

        User savedUser = userRepository.save(user);
        return AdminUserResponse.fromEntity(savedUser);
    }

    @Transactional
    public AdminUserResponse updateUserStatus(Long id, String status, Long adminId) {
        validateAdmin(adminId);

        User user = getUserOrThrow(id);
        String normalizedStatus = normalizeRequired(status, "Trạng thái không được để trống").toUpperCase();

        if (!STATUS_ACTIVE.equals(normalizedStatus) && !STATUS_LOCKED.equals(normalizedStatus)) {
            throw new RuntimeException("Trạng thái tài khoản không hợp lệ");
        }

        if (id.equals(adminId) && STATUS_LOCKED.equals(normalizedStatus)) {
            throw new RuntimeException("Không thể khóa chính tài khoản admin đang đăng nhập");
        }

        if (ROLE_ADMIN.equalsIgnoreCase(user.getRole()) && STATUS_LOCKED.equals(normalizedStatus)) {
            long activeAdminCount = userRepository.countByRoleIgnoreCaseAndStatusIgnoreCase(ROLE_ADMIN, STATUS_ACTIVE);
            if (activeAdminCount <= 1) {
                throw new RuntimeException("Không thể khóa admin cuối cùng đang hoạt động");
            }
        }

        user.setStatus(normalizedStatus);
        User savedUser = userRepository.save(user);
        return AdminUserResponse.fromEntity(savedUser);
    }

    private void applyEditableFields(User user, AdminUserRequest request) {
        user.setFullName(normalize(request.getFullName()));
        user.setPhone(normalize(request.getPhone()));
        user.setAvatarUrl(normalize(request.getAvatarUrl()));
        user.setRole(normalizeRole(request.getRole()));
        user.setStatus(normalizeStatus(request.getStatus()));
    }

    private void validateAdmin(Long adminId) {
        if (adminId == null) {
            throw new RuntimeException("Thiếu adminId");
        }

        User admin = getUserOrThrow(adminId);
        if (!ROLE_ADMIN.equalsIgnoreCase(admin.getRole())) {
            throw new RuntimeException("Tài khoản không có quyền ADMIN");
        }

        if (STATUS_LOCKED.equalsIgnoreCase(admin.getStatus())) {
            throw new RuntimeException("Tài khoản admin đã bị khóa");
        }
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    private String normalizeRole(String role) {
        String normalizedRole = normalize(role);
        if (normalizedRole == null || normalizedRole.isEmpty()) {
            return ROLE_CUSTOMER;
        }

        normalizedRole = normalizedRole.toUpperCase();
        if (!ROLE_ADMIN.equals(normalizedRole) && !ROLE_CUSTOMER.equals(normalizedRole)) {
            throw new RuntimeException("Vai trò không hợp lệ");
        }
        return normalizedRole;
    }

    private String normalizeStatus(String status) {
        String normalizedStatus = normalize(status);
        if (normalizedStatus == null || normalizedStatus.isEmpty()) {
            return STATUS_ACTIVE;
        }

        normalizedStatus = normalizedStatus.toUpperCase();
        if (!STATUS_ACTIVE.equals(normalizedStatus) && !STATUS_LOCKED.equals(normalizedStatus)) {
            throw new RuntimeException("Trạng thái tài khoản không hợp lệ");
        }
        return normalizedStatus;
    }

    private String normalizeRequired(String value, String errorMessage) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            throw new RuntimeException(errorMessage);
        }
        return normalized;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
