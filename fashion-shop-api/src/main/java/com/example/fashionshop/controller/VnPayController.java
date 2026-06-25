package com.example.fashionshop.controller;

import com.example.fashionshop.integration.vnpay.dto.VnPayPaymentResponse;
import com.example.fashionshop.integration.vnpay.dto.VnPayReturnResult;
import com.example.fashionshop.integration.vnpay.service.VnPayService;
import com.example.fashionshop.integration.vnpay.view.VnPayReturnPageBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/vnpay")
@CrossOrigin("*")
public class VnPayController {

    private final VnPayService vnPayService;
    private final VnPayReturnPageBuilder vnPayReturnPageBuilder;

    public VnPayController(VnPayService vnPayService, VnPayReturnPageBuilder vnPayReturnPageBuilder) {
        this.vnPayService = vnPayService;
        this.vnPayReturnPageBuilder = vnPayReturnPageBuilder;
    }

    @PostMapping("/create/{orderId}")
    public VnPayPaymentResponse createPayment(@PathVariable Long orderId, HttpServletRequest request) {
        return vnPayService.createPaymentUrl(orderId, getClientIp(request));
    }

    @GetMapping(value = "/return", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> handleReturn(@RequestParam Map<String, String> params) {
        VnPayReturnResult result = vnPayService.handleReturn(params);
        String html = vnPayReturnPageBuilder.build(result);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}