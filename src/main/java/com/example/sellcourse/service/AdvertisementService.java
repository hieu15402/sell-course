package com.example.sellcourse.service;

import com.example.sellcourse.dto.event.NotificationEvent;
import com.example.sellcourse.dto.response.advertisement.AdsActiveResponse;
import com.example.sellcourse.dto.response.advertisement.AdsApproveResponse;
import com.example.sellcourse.dto.response.advertisement.AdsCreateResponse;
import com.example.sellcourse.dto.response.general.PageResponse;
import com.example.sellcourse.dto.resquest.advertisement.AdsApproveRequest;
import com.example.sellcourse.dto.resquest.advertisement.AdsCreateRequest;
import com.example.sellcourse.entities.Advertisement;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.AdsStatus;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.AdsMapper;
import com.example.sellcourse.repository.AdvertisementRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import com.example.sellcourse.utils.vnpay.PaymentInfo;
import com.example.sellcourse.utils.vnpay.VNPayUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdvertisementService {
    AdvertisementRepository advertisementRepository;
    UserRepository userRepository;
    CloudinaryService cloudinaryService;
    AdsMapper adsMapper;
    KafkaTemplate<String, Object> kafkaTemplate;
    VNPayUtil vnPayUtil;

    public AdsCreateResponse userCreateAds(AdsCreateRequest request, MultipartFile image)
    {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String imageUrl = cloudinaryService.uploadImage(image);
        request.setImage(imageUrl);

        Advertisement advertisement = adsMapper.toAdvertisementEntity(request);
        advertisement.setUser(user);
        advertisement.setImage(request.getImage());

        advertisementRepository.save(advertisement);

        return adsMapper.toAdsCreationResponse(advertisement);
    }

    public AdsApproveResponse approveAds(AdsApproveRequest request) {
        Advertisement advertisement = advertisementRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADVERTISEMENT_ID_INVALID));

        String qrCodeUrl = "https://lh6.googleusercontent.com/proxy/BekiCiO_nYW9g-JB-72dP3FnxBDXFvi7rT7gYP7cTiVKXggT_88-QscAL6sK_2mBjTTpWzMc2lCaPjs7qCHU94QvXov45qQiNmyrW8MGAzc38PBmzElAFgLjMscpMbMwrC0mKXVcQrguzyFe4VClo6SVHudUQkxFjk5qk_vBR67K8DI3Gp5wcDDbvULai7Soq8GvcJA620h1bZ5lj-02WsRWHumb94_wPDko0mjeZw7KYrDKebkrst6XUPRbQDvEaZMIZfF572y2Y30iVD9HzbmOpX_fK605or7WKhFKaEmirqPMxaAHiUuYXIUoQ-z2CJ9pqzO7gVg-nYL-Ka-yL3SB3Mj1641byJnmr8tRuAuQUkF0pKHmbfAytWbamyE";

        advertisement.setApprovalStatus(AdsStatus.AWAITING_PAYMENT);
        advertisementRepository.save(advertisement);

        PaymentInfo paymentInfo = new PaymentInfo()
                .setReference("ADS_" + advertisement.getId())
                .setAmount(advertisement.getPrice().multiply(BigDecimal.valueOf(100)))
                .setDescription("Chay quang cao")
                .setExpiresIn(Duration.ofDays(1))
                .setIpAddress("0:0:0:0:0:0:0:1");

        Map<String, Object> emailData = new HashMap<>();
        emailData.put("title", advertisement.getTitle());
        emailData.put("description", advertisement.getDescription());
        emailData.put("img", advertisement.getImage());
        emailData.put("link", advertisement.getLink());
        emailData.put("startDate", advertisement.getStartDate().toString());
        emailData.put("endDate", advertisement.getEndDate().toString());
        emailData.put("priceAds", advertisement.getPrice() + " VND");
        emailData.put("qrCodeUrl", qrCodeUrl);
        emailData.put("status", advertisement.getApprovalStatus().name());
        emailData.put("paymentUrl",vnPayUtil.getPaymentURL(paymentInfo));

        NotificationEvent event = NotificationEvent.builder()
                .channel("Ads")
                .recipient(advertisement.getContactEmail())
                .templateCode("info-ads")
                .subject("Congratulations! Your ad has been approved.")
                .param(emailData)
                .build();

        kafkaTemplate.send("notification-delivery", event);

        return adsMapper.toAdsApproveResponse(advertisement);
    }

    @PreAuthorize("isAuthenticated()")
    public PageResponse<AdsCreateResponse> getAdsByCurrentLogin(int page, int size){
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Advertisement> advertisements = advertisementRepository
                .findAdvertisementByUserId(user.getId(), pageable);

        return PageResponse.<AdsCreateResponse>builder()
                .currentPage(page)
                .pageSize(pageable.getPageSize())
                .totalPages(advertisements.getTotalPages())
                .totalElements(advertisements.getTotalElements())
                .data(advertisements.getContent().stream()
                        .map(adsMapper::toAdsCreationResponse).toList())
                .build();
    }

    public List<AdsActiveResponse> getAdsWithActive(){
        List<Advertisement> advertisement = advertisementRepository
                .findAdvertisementByApprovalStatusActive("ACTIVE");

        return advertisement.stream().map(adsMapper::toAdsActiveResponse)
                .toList();
    }
}
