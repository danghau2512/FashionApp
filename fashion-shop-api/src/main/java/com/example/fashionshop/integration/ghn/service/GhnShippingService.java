package com.example.fashionshop.integration.ghn.service;

import com.example.fashionshop.dto.ShippingQuoteRequest;
import com.example.fashionshop.dto.ShippingQuoteResponse;
import com.example.fashionshop.entity.CartItem;
import com.example.fashionshop.integration.ghn.client.GhnClient;
import com.example.fashionshop.integration.ghn.dto.GhnAvailableService;
import com.example.fashionshop.integration.ghn.dto.GhnFeeData;
import com.example.fashionshop.integration.ghn.dto.GhnLeadtimeData;
import com.example.fashionshop.repository.CartItemRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class GhnShippingService {

    private final GhnClient ghnClient;
    private final CartItemRepository cartItemRepository;

    private final Integer defaultItemWeight;
    private final Integer packageLength;
    private final Integer packageWidth;
    private final Integer packageHeight;

    public GhnShippingService(
            GhnClient ghnClient,
            CartItemRepository cartItemRepository,
            @Value("${ghn.default-item-weight:300}") Integer defaultItemWeight,
            @Value("${ghn.package-length:25}") Integer packageLength,
            @Value("${ghn.package-width:20}") Integer packageWidth,
            @Value("${ghn.package-height:10}") Integer packageHeight
    ) {
        this.ghnClient = ghnClient;
        this.cartItemRepository = cartItemRepository;
        this.defaultItemWeight = defaultItemWeight;
        this.packageLength = packageLength;
        this.packageWidth = packageWidth;
        this.packageHeight = packageHeight;
    }

    public ShippingQuoteResponse getQuote(ShippingQuoteRequest request) {
        List<Long> cartItemIds = request.getCartItemIds()
                .stream()
                .distinct()
                .toList();

        List<CartItem> cartItems = cartItemRepository
                .findByUser_IdAndIdIn(request.getUserId(), cartItemIds);

        if (cartItems.size() != cartItemIds.size()) {
            throw new RuntimeException(
                    "Có sản phẩm không tồn tại hoặc không thuộc giỏ hàng"
            );
        }

        int totalQuantity = 0;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getQuantity() != null) {
                totalQuantity += cartItem.getQuantity();
            }
        }

        int totalWeight = Math.max(
                defaultItemWeight,
                totalQuantity * defaultItemWeight
        );

        List<GhnAvailableService> services =
                ghnClient.getAvailableServices(request.getDistrictId());

        if (services == null || services.isEmpty()) {
            throw new RuntimeException(
                    "GHN chưa hỗ trợ vận chuyển tới địa chỉ này"
            );
        }

        GhnAvailableService selectedService = selectStandardService(services);

        GhnFeeData feeData = ghnClient.calculateFee(
                request.getDistrictId(),
                request.getWardCode(),
                selectedService.getServiceId(),
                totalWeight,
                packageLength,
                packageWidth,
                packageHeight
        );

        GhnLeadtimeData leadtimeData = ghnClient.calculateLeadtime(
                request.getDistrictId(),
                request.getWardCode(),
                selectedService.getServiceId()
        );

        if (feeData == null || feeData.getTotal() == null) {
            throw new RuntimeException("GHN không trả về phí vận chuyển");
        }

        String estimatedDelivery =
                formatLeadtime(leadtimeData.getLeadtime());

        return new ShippingQuoteResponse(
                feeData.getTotal(),
                estimatedDelivery,
                selectedService.getServiceId(),
                selectedService.getShortName()
        );
    }

    private GhnAvailableService selectStandardService(
            List<GhnAvailableService> services
    ) {
        for (GhnAvailableService service : services) {
            if (Integer.valueOf(2).equals(service.getServiceTypeId())) {
                return service;
            }
        }

        return services.get(0);
    }

    private String formatLeadtime(Long leadtime) {
        if (leadtime == null) {
            return "Chưa xác định";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd/MM/yyyy",
                new Locale("vi", "VN")
        );

        return Instant.ofEpochSecond(leadtime)
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(formatter);
    }
}