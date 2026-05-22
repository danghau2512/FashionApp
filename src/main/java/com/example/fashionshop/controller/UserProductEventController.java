package com.example.fashionshop.controller;

import com.example.fashionshop.dto.CreateEventRequest;
import com.example.fashionshop.dto.EventResponse;
import com.example.fashionshop.service.UserProductEventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin("*")
public class UserProductEventController {

    private final UserProductEventService eventService;

    public UserProductEventController(UserProductEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping("/user/{userId}")
    public List<EventResponse> getEventsByUserId(@PathVariable Long userId) {
        return eventService.getEventsByUserId(userId);
    }
}