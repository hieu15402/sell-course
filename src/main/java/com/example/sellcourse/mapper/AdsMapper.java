package com.example.sellcourse.mapper;

import com.example.sellcourse.dto.response.advertisement.AdsActiveResponse;
import com.example.sellcourse.dto.response.advertisement.AdsApproveResponse;
import com.example.sellcourse.dto.response.advertisement.AdsCreateResponse;
import com.example.sellcourse.dto.resquest.advertisement.AdsCreateRequest;
import com.example.sellcourse.entities.Advertisement;
import com.example.sellcourse.enums.AdsStatus;
import com.example.sellcourse.repository.AdvertisementRepository;
import com.example.sellcourse.repository.user.UserRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class AdsMapper {

    AdvertisementRepository advertisementRepository;
    UserRepository userRepository;

    public AdsMapper(AdvertisementRepository advertisementRepository, UserRepository userRepository) {
        this.advertisementRepository = advertisementRepository;
        this.userRepository = userRepository;
    }

    public Advertisement toAdvertisementEntity(AdsCreateRequest request) {

        return Advertisement.builder()
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .title(request.getTitle())
                .image(request.getImage())
                .link(request.getLink())
                .description(request.getDescription())
                .location(request.getLocation())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .price(totalOfMoneyAds(request.getStartDate(), request.getEndDate()))
                .approvalStatus(AdsStatus.PENDING)
                .build();
    }

    public AdsCreateResponse toAdsCreationResponse(Advertisement advertisement) {
        return AdsCreateResponse.builder()
                .id(advertisement.getId())
                .contactEmail(advertisement.getContactEmail())
                .contactPhone(advertisement.getContactPhone())
                .title(advertisement.getTitle())
                .description(advertisement.getDescription())
                .startDate(advertisement.getStartDate())
                .endDate(advertisement.getEndDate())
                .priceAds(totalOfMoneyAds(advertisement.getStartDate(), advertisement.getEndDate()))
                .location(advertisement.getLocation())
                .imageUrl(advertisement.getImage())
                .link(advertisement.getLink())
                .status(advertisement.getApprovalStatus())
                .createAt(advertisement.getCreatedAt())
                .build();
    }

    public AdsApproveResponse toAdsApproveResponse(Advertisement advertisement) {
        return AdsApproveResponse.builder()
                .id(advertisement.getId())
                .contactEmail(advertisement.getContactEmail())
                .contactPhone(advertisement.getContactPhone())
                .title(advertisement.getTitle())
                .description(advertisement.getDescription())
                .link(advertisement.getLink())
                .imageUrl(advertisement.getImage())
                .startDate(advertisement.getStartDate())
                .endDate(advertisement.getEndDate())
                .priceAds(totalOfMoneyAds(advertisement.getStartDate(), advertisement.getEndDate()))
                .status(advertisement.getApprovalStatus())
                .build();
    }

    public AdsActiveResponse toAdsActiveResponse (Advertisement advertisement){

        return AdsActiveResponse.builder()
                .id(advertisement.getId())
                .title(advertisement.getTitle())
                .image(advertisement.getImage())
                .price(advertisement.getPrice())
                .description(advertisement.getDescription())
                .link(advertisement.getLink())
                .startDate(advertisement.getStartDate())
                .endDate(advertisement.getEndDate())
                .build();
    }

    private BigDecimal totalOfMoneyAds (LocalDate startDate, LocalDate endDate){
        long totalDay = ChronoUnit.DAYS.between(startDate, endDate);

        BigDecimal dailyRate  = BigDecimal.valueOf(100000);

        return dailyRate.multiply(BigDecimal.valueOf(totalDay));
    }
}
