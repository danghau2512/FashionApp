package com.example.fashionshop.integration.vnpay.service;

import com.example.fashionshop.entity.ShopOrder;
import com.example.fashionshop.integration.vnpay.dto.VnPayPaymentResponse;
import com.example.fashionshop.integration.vnpay.dto.VnPayReturnResult;
import com.example.fashionshop.repository.ShopOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

@Service
public class VnPayService {

    private final ShopOrderRepository shopOrderRepository;
    private final String paymentUrl;
    private final String tmnCode;
    private final String hashSecret;
    private final String returnUrl;

    public VnPayService(ShopOrderRepository shopOrderRepository, @Value("${vnpay.payment-url}") String paymentUrl, @Value("${vnpay.tmn-code}") String tmnCode, @Value("${vnpay.hash-secret}") String hashSecret, @Value("${vnpay.return-url}") String returnUrl) {
        this.shopOrderRepository = shopOrderRepository;
        this.paymentUrl = paymentUrl;
        this.tmnCode = tmnCode;
        this.hashSecret = hashSecret;
        this.returnUrl = returnUrl;
    }

    public VnPayPaymentResponse createPaymentUrl(Long orderId, String ipAddress) {
        ShopOrder order = shopOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!"VNPAY".equals(order.getPaymentMethod())) {
            throw new RuntimeException("Đơn hàng không sử dụng phương thức VNPAY");
        }

        if ("PAID".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Đơn hàng đã được thanh toán");
        }

        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new RuntimeException("Đơn hàng không còn chờ xử lý");
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        String amount = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toPlainString();

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", amount);
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(order.getId()));
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", ipAddress == null || ipAddress.isBlank() ? "127.0.0.1" : ipAddress);
        params.put("vnp_CreateDate", now.format(formatter));
        params.put("vnp_ExpireDate", now.plusMinutes(15).format(formatter));

        String hashData = buildQueryString(params);
        String secureHash = hmacSha512(hashSecret, hashData);
        String url = paymentUrl + "?" + hashData + "&vnp_SecureHash=" + secureHash;

        return new VnPayPaymentResponse(order.getId(), url, order.getPaymentStatus());
    }

    @Transactional
    public VnPayReturnResult handleReturn(Map<String, String> requestParams) {
        String receivedHash = requestParams.get("vnp_SecureHash");
        Long orderId = parseOrderId(requestParams.get("vnp_TxnRef"));

        if (receivedHash == null || orderId == null) {
            return new VnPayReturnResult(orderId, false, "Thông tin thanh toán không hợp lệ");
        }

        Map<String, String> fields = new TreeMap<>(requestParams);
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String calculatedHash = hmacSha512(hashSecret, buildQueryString(fields));

        if (!calculatedHash.equalsIgnoreCase(receivedHash)) {
            return new VnPayReturnResult(orderId, false, "Chữ ký VNPAY không hợp lệ");
        }

        ShopOrder order = shopOrderRepository.findById(orderId).orElse(null);

        if (order == null) {
            return new VnPayReturnResult(orderId, false, "Không tìm thấy đơn hàng");
        }

        String expectedAmount = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toPlainString();
        String returnedAmount = requestParams.get("vnp_Amount");

        if (!expectedAmount.equals(returnedAmount)) {
            return new VnPayReturnResult(orderId, false, "Số tiền thanh toán không khớp");
        }

        if ("PAID".equals(order.getPaymentStatus())) {
            return new VnPayReturnResult(orderId, true, "Đơn hàng đã được thanh toán");
        }

        String responseCode = requestParams.get("vnp_ResponseCode");
        String transactionStatus = requestParams.get("vnp_TransactionStatus");
        boolean success = "00".equals(responseCode) && "00".equals(transactionStatus);

        if (success) {
            order.setPaymentStatus("PAID");
            shopOrderRepository.save(order);
            return new VnPayReturnResult(orderId, true, "Thanh toán thành công");
        }

        order.setPaymentStatus("FAILED");
        shopOrderRepository.save(order);
        return new VnPayReturnResult(orderId, false, "Thanh toán không thành công");
    }

    private Long parseOrderId(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception exception) {
            return null;
        }
    }

    private String buildQueryString(Map<String, String> params) {
        StringJoiner joiner = new StringJoiner("&");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }

            joiner.add(encode(entry.getKey()) + "=" + encode(entry.getValue()));
        }

        return joiner.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String hmacSha512(String secretKey, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(keySpec);
            byte[] result = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(result);
        } catch (Exception exception) {
            throw new RuntimeException("Không thể tạo chữ ký VNPAY", exception);
        }
    }
}