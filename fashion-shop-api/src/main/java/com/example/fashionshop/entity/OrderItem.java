package com.example.fashionshop.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private ShopOrder order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @Column(name = "product_name")
    private String productName;

    private String size;

    private String color;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;

    public OrderItem() {
    }

    public Long getId() {
        return id;
    }

    public ShopOrder getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public String getProductName() {
        return productName;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setOrder(ShopOrder order) {
        this.order = order;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}