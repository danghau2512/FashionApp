package com.example.fashionshop.controller;

import com.example.fashionshop.dto.AdminUserRequest;
import com.example.fashionshop.dto.AdminUserResponse;
import com.example.fashionshop.dto.AdminUserStatusRequest;
import com.example.fashionshop.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin("*")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<AdminUserResponse> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status
    ) {
        return adminUserService.getUsers(keyword, role, status);
    }

    @GetMapping("/{id}")
    public AdminUserResponse getUserById(@PathVariable Long id) {
        return adminUserService.getUserById(id);
    }

    @PostMapping
    public AdminUserResponse createUser(
            @RequestParam Long adminId,
            @RequestBody AdminUserRequest request
    ) {
        return adminUserService.createUser(request, adminId);
    }

    @PutMapping("/{id}")
    public AdminUserResponse updateUser(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody AdminUserRequest request
    ) {
        return adminUserService.updateUser(id, request, adminId);
    }

    @PutMapping("/{id}/status")
    public AdminUserResponse updateUserStatus(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody AdminUserStatusRequest request
    ) {
        return adminUserService.updateUserStatus(id, request.getStatus(), adminId);
    }
}
