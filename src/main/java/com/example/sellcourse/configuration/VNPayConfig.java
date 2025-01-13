package com.example.sellcourse.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayConfig {
    String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    String vnp_ReturnUrl = "http://localhost:8080/api/v1/payments/vn-pay-callback";
    String vnp_TmnCode = "DZXR4R09";
    String secretKey = "BI7JKL4XBG4MNXGHV967UOVDGVZC98P1";
    String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    String vnp_Version = "2.1.0";
    String vnp_Command = "pay";
    String orderType = "other";
    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", this.vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        return vnpParamsMap;
    }
}
