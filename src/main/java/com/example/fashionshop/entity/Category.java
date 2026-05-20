package com.example.fashionshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    private String status;

    public Category() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}