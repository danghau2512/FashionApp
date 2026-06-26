package com.example.fashionshop.controller;

import com.example.fashionshop.dto.UserResponse;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserAvatarUploadController {

    private final UserRepository userRepository;

    public UserAvatarUploadController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/{userId}/avatar")
    public UserResponse uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng"
                ));

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File ảnh không được để trống"
            );
        }

        String originalFileName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : ""
        );

        String extension = getExtension(originalFileName);

        if (!isAllowedImageExtension(extension)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ cho phép upload ảnh jpg, jpeg, png, webp"
            );
        }

        try {
            Path uploadDir = Paths.get("uploads", "images").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            String fileName = UUID.randomUUID() + "." + extension;
            Path targetPath = uploadDir.resolve(fileName);

            file.transferTo(targetPath.toFile());

            String avatarUrl = "/uploads/images/" + fileName;

            user.setAvatarUrl(avatarUrl);
            User savedUser = userRepository.save(user);

            return UserResponse.fromEntity(savedUser);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể lưu ảnh: " + e.getMessage()
            );
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File ảnh không hợp lệ"
            );
        }

        return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private boolean isAllowedImageExtension(String extension) {
        return "jpg".equals(extension)
                || "jpeg".equals(extension)
                || "png".equals(extension)
                || "webp".equals(extension);
    }
}