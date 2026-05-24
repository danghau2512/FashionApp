package com.example.fashionshop.service;

import com.example.fashionshop.dto.ProductVariantResponse;
import com.example.fashionshop.entity.ProductVariant;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    public ProductVariantService(ProductVariantRepository productVariantRepository,
                                 ProductRepository productRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
    }

    public List<ProductVariantResponse> getVariantsByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }

        return productVariantRepository.findByProduct_IdAndStatus(productId, "ACTIVE")
                .stream()
                .map(this::toProductVariantResponse)
                .toList();
    }

    public ProductVariantResponse getVariantById(Long id) {
        ProductVariant variant = productVariantRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm"));

        return toProductVariantResponse(variant);
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
}