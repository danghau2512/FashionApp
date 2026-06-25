package com.example.fashionshop.service;

import com.example.fashionshop.dto.AdminProductVariantRequest;
import com.example.fashionshop.dto.AdminProductVariantResponse;
import com.example.fashionshop.dto.ProductVariantResponse;
import com.example.fashionshop.entity.ProductVariant;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.repository.ProductVariantRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.fashionshop.entity.Product;

import java.util.List;

@Service
public class ProductVariantService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";
    private static final String ROLE_ADMIN = "ADMIN";

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductVariantService(ProductVariantRepository productVariantRepository,
                                 ProductRepository productRepository,
                                 UserRepository userRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // API khách hàng
    // =========================

    public List<ProductVariantResponse> getVariantsByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }

        return productVariantRepository.findByProduct_IdAndStatus(productId, STATUS_ACTIVE)
                .stream()
                .map(this::toProductVariantResponse)
                .toList();
    }

    public ProductVariantResponse getVariantById(Long id) {
        ProductVariant variant = productVariantRepository.findByIdAndStatus(id, STATUS_ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể sản phẩm"));

        return toProductVariantResponse(variant);
    }

    // =========================
    // API admin
    // =========================

    public List<AdminProductVariantResponse> getAdminVariantsByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }

        return productVariantRepository.findByProduct_IdOrderByIdDesc(productId)
                .stream()
                .map(this::toAdminProductVariantResponse)
                .toList();
    }

    public AdminProductVariantResponse getAdminVariantById(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể sản phẩm"));

        return toAdminProductVariantResponse(variant);
    }

    @Transactional
    public AdminProductVariantResponse createVariant(Long productId,
                                                     AdminProductVariantRequest request,
                                                     Long adminId) {
        checkAdmin(adminId);
        validateVariantRequest(request);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSize(request.getSize());
        variant.setColor(request.getColor());
        variant.setQuantity(request.getQuantity());
        variant.setImageUrl(request.getImageUrl());
        variant.setStatus(normalizeStatusOrDefault(request.getStatus()));

        ProductVariant savedVariant = productVariantRepository.save(variant);
        return toAdminProductVariantResponse(savedVariant);
    }

    @Transactional
    public AdminProductVariantResponse updateVariant(Long id,
                                                     AdminProductVariantRequest request,
                                                     Long adminId) {
        checkAdmin(adminId);
        validateVariantRequest(request);

        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể sản phẩm"));

        variant.setSize(request.getSize());
        variant.setColor(request.getColor());
        variant.setQuantity(request.getQuantity());
        variant.setImageUrl(request.getImageUrl());
        variant.setStatus(normalizeStatusOrDefault(request.getStatus()));

        ProductVariant savedVariant = productVariantRepository.save(variant);
        return toAdminProductVariantResponse(savedVariant);
    }

    @Transactional
    public AdminProductVariantResponse updateVariantStatus(Long id, String status, Long adminId) {
        checkAdmin(adminId);

        String normalizedStatus = normalizeRequiredStatus(status);

        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể sản phẩm"));

        variant.setStatus(normalizedStatus);

        ProductVariant savedVariant = productVariantRepository.save(variant);
        return toAdminProductVariantResponse(savedVariant);
    }

    // =========================
    // Helper
    // =========================

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

    private void validateVariantRequest(AdminProductVariantRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu biến thể không được để trống");
        }

        if (request.getSize() == null || request.getSize().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size không được để trống");
        }

        if (request.getColor() == null || request.getColor().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Màu không được để trống");
        }

        if (request.getQuantity() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng không được để trống");
        }

        if (request.getQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng không được âm");
        }

        normalizeStatusOrDefault(request.getStatus());
    }

    private String normalizeStatusOrDefault(String status) {
        if (status == null || status.trim().isEmpty()) {
            return STATUS_ACTIVE;
        }

        return normalizeRequiredStatus(status);
    }

    private String normalizeRequiredStatus(String status) {
        String normalizedStatus = status.trim().toUpperCase();

        if (!STATUS_ACTIVE.equals(normalizedStatus) && !STATUS_INACTIVE.equals(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái chỉ được là ACTIVE hoặc INACTIVE");
        }

        return normalizedStatus;
    }

    private ProductVariantResponse toProductVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
                variant.getProduct() != null ? variant.getProduct().getId() : null,
                variant.getSize(),
                variant.getColor(),
                variant.getQuantity(),
                variant.getImageUrl()
        );
    }

    private AdminProductVariantResponse toAdminProductVariantResponse(ProductVariant variant) {
        return new AdminProductVariantResponse(
                variant.getId(),
                variant.getProduct() != null ? variant.getProduct().getId() : null,
                variant.getProduct() != null ? variant.getProduct().getName() : null,
                variant.getSize(),
                variant.getColor(),
                variant.getQuantity(),
                variant.getImageUrl(),
                variant.getStatus()
        );
    }
}