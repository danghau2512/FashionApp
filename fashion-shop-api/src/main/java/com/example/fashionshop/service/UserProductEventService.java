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

import java.util.List;

@Service
public class UserProductEventService {

    private final UserProductEventRepository eventRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public UserProductEventService(UserProductEventRepository eventRepository,
                                   UserRepository userRepository,
                                   ProductRepository productRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public EventResponse createEvent(CreateEventRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new RuntimeException("Sản phẩm hiện không hoạt động");
        }

        String eventType = request.getEventType().toUpperCase();

        Integer score = getScoreByEventType(eventType);

        UserProductEvent event = new UserProductEvent();
        event.setUser(user);
        event.setProduct(product);
        event.setEventType(eventType);
        event.setScore(score);

        UserProductEvent savedEvent = eventRepository.save(event);

        return toEventResponse(savedEvent);
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