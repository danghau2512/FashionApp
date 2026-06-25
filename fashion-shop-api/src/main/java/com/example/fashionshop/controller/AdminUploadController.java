package com.example.fashionshop.controller;

import com.example.fashionshop.dto.ImageUploadResponse;
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
@RequestMapping("/api/admin/uploads")
@CrossOrigin("*")
public class AdminUploadController {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String STATUS_ACTIVE = "ACTIVE";

    private final UserRepository userRepository;

    public AdminUploadController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/images")
    public ImageUploadResponse uploadImage(
            @RequestParam Long adminId,
            @RequestParam("file") MultipartFile file
    ) {
        checkAdmin(adminId);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File ảnh không được để trống");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "");

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

            String imageUrl = "/uploads/images/" + fileName;
            return new ImageUploadResponse(imageUrl);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể lưu ảnh: " + e.getMessage()
            );
        }
    }

    private void checkAdmin(Long adminId) {
        if (adminId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu adminId");
        }

        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin không tồn tại"));

        if (!ROLE_ADMIN.equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản không có quyền ADMIN");
        }

        if (!STATUS_ACTIVE.equals(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản admin không còn hoạt động");
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File ảnh không hợp lệ");
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