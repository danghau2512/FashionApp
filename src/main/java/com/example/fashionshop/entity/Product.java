package com.example.fashionshop.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "image_url")
    private String imageUrl;

    private String brand;
    private String gender;
    private String status;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "sold_count")
    private Integer soldCount;

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public String getGender() {
        return gender;
    }

    public String getStatus() {
        return status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public Integer getSoldCount() {
        return soldCount;
    }
}