package com.example.fashionshop.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_recommendations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_recommendation_user_product", columnNames = {"user_id", "product_id"})
})
public class ProductRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "recommendation_score", nullable = false)
    private Double recommendationScore;

    @Column(name = "rank_number", nullable = false)
    private Integer rankNumber;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public ProductRecommendation() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    public Double getRecommendationScore() {
        return recommendationScore;
    }

    public Integer getRankNumber() {
        return rankNumber;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}