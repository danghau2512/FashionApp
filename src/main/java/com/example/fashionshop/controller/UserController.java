package com.example.fashionshop.controller;

import com.example.fashionshop.dto.UserResponse;
import com.example.fashionshop.dto.UserSyncRequest;
import com.example.fashionshop.dto.UserUpdateRequest;
import com.example.fashionshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sync")
    public UserResponse syncUser(@Valid @RequestBody UserSyncRequest request) {
        return userService.syncUser(request);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/firebase/{firebaseUid}")
    public UserResponse getUserByFirebaseUid(@PathVariable String firebaseUid) {
        return userService.getUserByFirebaseUid(firebaseUid);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }
}