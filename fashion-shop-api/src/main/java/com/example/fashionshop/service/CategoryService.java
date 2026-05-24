package com.example.fashionshop.service;

import com.example.fashionshop.dto.CategoryResponse;
import com.example.fashionshop.entity.Category;
import com.example.fashionshop.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByStatus("ACTIVE")
                .stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        return toCategoryResponse(category);
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl()
        );
    }
}