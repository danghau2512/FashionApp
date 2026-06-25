package com.example.fashionshop.service;

import com.example.fashionshop.dto.AdminProductRequest;
import com.example.fashionshop.dto.AdminProductResponse;
import com.example.fashionshop.dto.ProductResponse;
import com.example.fashionshop.entity.Category;
import com.example.fashionshop.entity.Product;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.CategoryRepository;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";
    private static final String ROLE_ADMIN = "ADMIN";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // API khách hàng
    // =========================

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByStatus(STATUS_ACTIVE)
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        if (!STATUS_ACTIVE.equals(product.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }

        return toProductResponse(product);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndStatus(keyword, STATUS_ACTIVE)
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_IdAndStatus(categoryId, STATUS_ACTIVE)
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    // =========================
    // API admin
    // =========================

    public List<AdminProductResponse> getAdminProducts(String keyword, String status, Long categoryId) {
        String normalizedStatus = normalizeOptionalStatus(status);

        return productRepository.searchAdminProducts(keyword, normalizedStatus, categoryId)
                .stream()
                .map(this::toAdminProductResponse)
                .toList();
    }

    public AdminProductResponse getAdminProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        return toAdminProductResponse(product);
    }

    @Transactional
    public AdminProductResponse createProduct(AdminProductRequest request, Long adminId) {
        checkAdmin(adminId);
        validateProductRequest(request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại"));

        Product product = new Product();
        product.setCategory(category);
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSalePrice(request.getSalePrice());
        product.setImageUrl(request.getImageUrl());
        product.setBrand(request.getBrand());
        product.setGender(request.getGender());
        product.setStatus(normalizeStatusOrDefault(request.getStatus()));
        product.setViewCount(0);
        product.setSoldCount(0);

        Product savedProduct = productRepository.save(product);
        return toAdminProductResponse(savedProduct);
    }

    @Transactional
    public AdminProductResponse updateProduct(Long id, AdminProductRequest request, Long adminId) {
        checkAdmin(adminId);
        validateProductRequest(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại"));

        product.setCategory(category);
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSalePrice(request.getSalePrice());
        product.setImageUrl(request.getImageUrl());
        product.setBrand(request.getBrand());
        product.setGender(request.getGender());
        product.setStatus(normalizeStatusOrDefault(request.getStatus()));

        Product savedProduct = productRepository.save(product);
        return toAdminProductResponse(savedProduct);
    }

    @Transactional
    public AdminProductResponse updateProductStatus(Long id, String status, Long adminId) {
        checkAdmin(adminId);

        String normalizedStatus = normalizeRequiredStatus(status);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        product.setStatus(normalizedStatus);

        Product savedProduct = productRepository.save(product);
        return toAdminProductResponse(savedProduct);
    }

    // =========================
    // Validate + helper
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

    private void validateProductRequest(AdminProductRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu sản phẩm không được để trống");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên sản phẩm không được để trống");
        }

        if (request.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh mục không được để trống");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá sản phẩm phải lớn hơn 0");
        }

        if (request.getSalePrice() != null) {
            if (request.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá sale không được âm");
            }

            if (request.getSalePrice().compareTo(request.getPrice()) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá sale không được lớn hơn giá gốc");
            }
        }

        normalizeStatusOrDefault(request.getStatus());
    }

    private String normalizeStatusOrDefault(String status) {
        if (status == null || status.trim().isEmpty()) {
            return STATUS_ACTIVE;
        }

        return normalizeRequiredStatus(status);
    }

    private String normalizeOptionalStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
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

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSalePrice(),
                product.getImageUrl(),
                product.getBrand(),
                product.getGender(),
                product.getViewCount(),
                product.getSoldCount()
        );
    }

    private AdminProductResponse toAdminProductResponse(Product product) {
        return new AdminProductResponse(
                product.getId(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSalePrice(),
                product.getImageUrl(),
                product.getBrand(),
                product.getGender(),
                product.getStatus(),
                product.getViewCount(),
                product.getSoldCount()
        );
    }
}