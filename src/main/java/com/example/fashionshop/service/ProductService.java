package com.example.fashionshop.service;

import com.example.fashionshop.dto.ProductResponse;
import com.example.fashionshop.entity.Product;
import com.example.fashionshop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByStatus("ACTIVE")
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        return toProductResponse(product);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndStatus(keyword, "ACTIVE")
                .stream()
                .map(this::toProductResponse)
                .toList();
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
}