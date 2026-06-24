package com.example.fashionshop.service;

import com.example.fashionshop.dto.CreateProductReviewRequest;
import com.example.fashionshop.dto.ProductReviewResponse;
import com.example.fashionshop.dto.ReviewEligibilityResponse;
import com.example.fashionshop.dto.ReviewableOrderItemResponse;
import com.example.fashionshop.entity.OrderItem;
import com.example.fashionshop.entity.Product;
import com.example.fashionshop.entity.ProductReview;
import com.example.fashionshop.entity.ShopOrder;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.OrderItemRepository;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.repository.ProductReviewRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductReviewService(ProductReviewRepository productReviewRepository,
                                UserRepository userRepository,
                                ProductRepository productRepository,
                                OrderItemRepository orderItemRepository) {
        this.productReviewRepository = productReviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<ProductReviewResponse> getReviewsByProductId(Long productId) {
        return productReviewRepository.findByProduct_IdAndStatusOrderByCreatedAtDesc(productId, "ACTIVE")
                .stream()
                .map(this::toProductReviewResponse)
                .toList();
    }

    public ReviewEligibilityResponse getReviewEligibility(Long userId, Long productId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }

        List<ReviewableOrderItemResponse> reviewableItems = productReviewRepository
                .findReviewableOrderItems(userId, productId)
                .stream()
                .map(this::toReviewableOrderItemResponse)
                .toList();

        Double averageRating = productReviewRepository.getAverageRatingByProductId(productId);
        Long reviewCount = productReviewRepository.countByProduct_IdAndStatus(productId, "ACTIVE");

        return new ReviewEligibilityResponse(
                !reviewableItems.isEmpty(),
                reviewableItems.size(),
                averageRating,
                reviewCount,
                reviewableItems
        );
    }

    @Transactional
    public ProductReviewResponse createReview(CreateProductReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong đơn hàng"));

        validateReviewPermission(user, product, orderItem);

        ProductReview review = new ProductReview();
        review.setUser(user);
        review.setProduct(product);
        review.setOrderItem(orderItem);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setStatus("ACTIVE");

        ProductReview savedReview = productReviewRepository.save(review);

        return toProductReviewResponse(savedReview);
    }

    private void validateReviewPermission(User user, Product product, OrderItem orderItem) {
        ShopOrder order = orderItem.getOrder();

        if (order == null || order.getUser() == null || !order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền đánh giá sản phẩm này");
        }

        if (orderItem.getProduct() == null || !orderItem.getProduct().getId().equals(product.getId())) {
            throw new RuntimeException("Sản phẩm không thuộc dòng đơn hàng đã chọn");
        }

        if (!isSuccessfulOrder(order.getOrderStatus())) {
            throw new RuntimeException("Chỉ được đánh giá khi đơn hàng đã thành công");
        }

        if (productReviewRepository.existsByOrderItem_Id(orderItem.getId())) {
            throw new RuntimeException("Bạn đã đánh giá lượt mua này rồi");
        }
    }

    private boolean isSuccessfulOrder(String orderStatus) {
        return "COMPLETED".equals(orderStatus) || "DELIVERED".equals(orderStatus);
    }

    private ProductReviewResponse toProductReviewResponse(ProductReview review) {
        User user = review.getUser();
        OrderItem orderItem = review.getOrderItem();

        return new ProductReviewResponse(
                review.getId(),
                user != null ? user.getId() : null,
                user != null ? user.getFullName() : null,
                user != null ? user.getAvatarUrl() : null,
                review.getProduct() != null ? review.getProduct().getId() : null,
                orderItem != null ? orderItem.getId() : null,
                orderItem != null ? orderItem.getSize() : null,
                orderItem != null ? orderItem.getColor() : null,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    private ReviewableOrderItemResponse toReviewableOrderItemResponse(OrderItem item) {
        return new ReviewableOrderItemResponse(
                item.getId(),
                item.getOrder() != null ? item.getOrder().getId() : null,
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProductName(),
                item.getSize(),
                item.getColor(),
                item.getQuantity(),
                item.getOrder() != null ? item.getOrder().getCreatedAt() : null
        );
    }
}