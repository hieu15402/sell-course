package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.response.vnpay.VNPAYResponse;
import com.example.sellcourse.entities.Advertisement;
import com.example.sellcourse.entities.Payment;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.AdsStatus;
import com.example.sellcourse.enums.PaymentStatus;
import com.example.sellcourse.enums.PaymentType;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.repository.AdvertisementRepository;
import com.example.sellcourse.repository.PaymentRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;
    PaymentRepository paymentRepository;
    UserRepository userRepository;
    AdvertisementRepository advertisementRepository;

    @GetMapping("/vn-pay")
    public ApiResponse<VNPAYResponse> pay(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("paymentType") String paymentType,
            HttpServletRequest request) {

        try {
            PaymentType type = PaymentType.fromValue(paymentType.trim());

            return ApiResponse.<VNPAYResponse>builder()
                    .code(HttpStatus.OK.value())
                    .message("OK")
                    .result(paymentService.createVnPayPayment(request, type))
                    .build();
        }
        catch (AppException exception){
            throw new AppException(ErrorCode.INVALID_PAYMENT_TYPE);
        }
    }



    @GetMapping("/vn-pay-callback")
    public void handleVnPayCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String redirectUrl;
        String transactionStatus = request.getParameter("vnp_ResponseCode");

        BigDecimal amountInVNPay = new BigDecimal(request.getParameter("vnp_Amount"));
        BigDecimal actualAmount = amountInVNPay.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

        if ("00".equals(transactionStatus)) {
            String paymentRef = request.getParameter("vnp_TxnRef");
            String[] refParts = paymentRef.split("_");
            String paymentType = refParts[0];

            PaymentType type = PaymentType.fromValue(paymentType);

            switch (type) {
                case DEPOSIT -> {
                    User user = userRepository.findById(Long.parseLong(refParts[1]))
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                    recordPaymentTransaction(user, actualAmount, PaymentStatus.COMPLETED);
                    BigDecimal pointsPer1000VND = new BigDecimal(10); // 1000VND = 10 points
                    BigDecimal numberOfThousands = actualAmount.divide(new BigDecimal(1000),
                            2, RoundingMode.HALF_UP);
                    BigDecimal totalPoints = numberOfThousands.multiply(pointsPer1000VND);

                    long pointsToAdd = totalPoints.longValue();

                    if (user.getPoints() == null) {
                        user.setPoints(pointsToAdd);
                    } else {
                        user.setPoints(user.getPoints() + pointsToAdd);
                    }
                    userRepository.save(user);
                }
                case ADVERTISEMENT -> {
                    Advertisement advertisement = advertisementRepository.findById(Long.parseLong(refParts[1]))
                            .orElseThrow(() -> new AppException(ErrorCode.ADVERTISEMENT_ID_INVALID));
                    if (actualAmount.compareTo(advertisement.getPrice()) >= 0) {
                        advertisement.setApprovalStatus(AdsStatus.ACTIVE);
                    }
                    advertisementRepository.save(advertisement);
                }
                default -> throw new AppException(ErrorCode.INVALID_PAYMENT_TYPE);
            }

            redirectUrl = "http://localhost:3000/payment-success";
        } else if ("24".equals(transactionStatus)) {
            redirectUrl = "http://localhost:3000/payment-cancel";
        } else {
            redirectUrl = "http://localhost:3000/payment-failed";
        }
        response.sendRedirect(redirectUrl);
    }

    private void recordPaymentTransaction(User user, BigDecimal amount, PaymentStatus status) {
        Payment payment = Payment.builder()
                .user(user)
                .price(amount)
                .status(status)
                .build();

        paymentRepository.save(payment);
    }

}
