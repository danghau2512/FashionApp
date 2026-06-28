package com.example.fashionshop.service;

import com.example.fashionshop.dto.CreateEventRequest;
import com.example.fashionshop.dto.EventResponse;
import com.example.fashionshop.entity.Product;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.entity.UserProductEvent;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.repository.UserProductEventRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.fashionshop.entity.OrderItem;
import com.example.fashionshop.repository.OrderItemRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Service
public class UserProductEventService {

    private final UserProductEventRepository eventRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    public UserProductEventService(UserProductEventRepository eventRepository,
                                   UserRepository userRepository,
                                   ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public EventResponse createEvent(CreateEventRequest request) {
        return recordEvent(request.getUserId(), request.getProductId(), request.getEventType());
    }
    public EventResponse recordEvent(Long userId, Long productId, String eventTypeValue) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new RuntimeException("Sản phẩm hiện không hoạt động");
        }

        String eventType = eventTypeValue.trim().toUpperCase();
        Integer score = getScoreByEventType(eventType);

        UserProductEvent event = new UserProductEvent();
        event.setUser(user);
        event.setProduct(product);
        event.setEventType(eventType);
        event.setScore(score);

        return toEventResponse(eventRepository.save(event));
    }
    public void recordPurchaseEvents(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(orderId);
        Set<Long> recordedProductIds = new HashSet<>();

        for (OrderItem item : orderItems) {
            if (item.getProduct() == null) {
                continue;
            }

            Long productId = item.getProduct().getId();

            if (recordedProductIds.add(productId)) {
                recordEvent(item.getOrder().getUser().getId(), productId, "PURCHASE");
            }
        }
    }

    public List<EventResponse> getEventsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        return eventRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toEventResponse)
                .toList();
    }

    private Integer getScoreByEventType(String eventType) {
        return switch (eventType) {
            case "VIEW" -> 1;
            case "ADD_TO_CART" -> 3;
            case "PURCHASE" -> 5;
            default -> throw new RuntimeException("Loại hành vi không hợp lệ");
        };
    }

    private EventResponse toEventResponse(UserProductEvent event) {
        return new EventResponse(
                event.getId(),
                event.getUser().getId(),
                event.getProduct().getId(),
                event.getProduct().getName(),
                event.getEventType(),
                event.getScore(),
                event.getCreatedAt()
        );
    }
}