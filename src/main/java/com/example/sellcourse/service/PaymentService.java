package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.vnpay.VNPAYResponse;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.PaymentType;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import com.example.sellcourse.utils.vnpay.PaymentInfo;
import com.example.sellcourse.utils.vnpay.ServletHelper;
import com.example.sellcourse.utils.vnpay.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    private final UserRepository userRepository;
    private final VNPayUtil vnPayUtil;

    public VNPAYResponse createVnPayPayment(HttpServletRequest request, PaymentType paymentType) {
        BigDecimal amount = new BigDecimal(request.getParameter("amount")).multiply(BigDecimal.valueOf(100));
        String email = SecurityUtils.getCurrentUserLogin().orElseThrow();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PaymentInfo paymentInfo = new PaymentInfo()
                .setReference(paymentType.getValue() + "_" + user.getId() + "_" + VNPayUtil.getRandomNumber(6))
                .setAmount(amount)
                .setDescription("Thanh toan")
                .setExpiresIn(Duration.ofMinutes(15))
                .setIpAddress(ServletHelper.extractIPAddress(request));
        String paymentUrl = vnPayUtil.getPaymentURL(paymentInfo);

        return VNPAYResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }
}
