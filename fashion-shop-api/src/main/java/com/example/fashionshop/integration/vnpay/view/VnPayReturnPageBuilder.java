package com.example.fashionshop.integration.vnpay.view;

import com.example.fashionshop.integration.vnpay.dto.VnPayReturnResult;
import org.springframework.stereotype.Component;

@Component
public class VnPayReturnPageBuilder {

    public String build(VnPayReturnResult result) {
        long orderId = result.getOrderId() == null ? 0 : result.getOrderId();
        String status = result.isSuccess() ? "success" : "failed";
        String title = result.isSuccess() ? "Thanh toán thành công" : "Thanh toán không thành công";
        String color = result.isSuccess() ? "#16a34a" : "#dc2626";
        String deepLink = result.isSuccess()
                ? "fashionshop://order-success?orderId=" + orderId
                : "fashionshop://home";
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Kết quả thanh toán</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            text-align: center;
                            padding: 40px 20px;
                            background: #f5f5f5;
                        }

                        .box {
                            max-width: 420px;
                            margin: auto;
                            padding: 28px;
                            background: white;
                            border-radius: 14px;
                        }

                        h2 {
                            color: %s;
                        }

                        .button {
                            display: inline-block;
                            margin-top: 20px;
                            padding: 13px 22px;
                            color: white;
                            background: #ee4d2d;
                            border-radius: 8px;
                            text-decoration: none;
                        }
                    </style>
                </head>
                <body>
                    <div class="box">
                        <h2>%s</h2>
                        <p>Mã đơn hàng: DH%s</p>
                        <p>%s</p>
                        <a class="button" href="%s">Quay lại ứng dụng</a>
                    </div>
                </body>
                </html>
                """.formatted(color, title, orderId, result.getMessage(), deepLink);
    }
}