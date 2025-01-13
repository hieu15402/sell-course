package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.advertisement.AdsActiveResponse;
import com.example.sellcourse.dto.response.advertisement.AdsApproveResponse;
import com.example.sellcourse.dto.response.advertisement.AdsCreateResponse;
import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.response.general.PageResponse;
import com.example.sellcourse.dto.resquest.advertisement.AdsApproveRequest;
import com.example.sellcourse.dto.resquest.advertisement.AdsCreateRequest;
import com.example.sellcourse.service.AdvertisementService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/advertisements")
@Slf4j
public class AdvertisementController {

    AdvertisementService advertisementService;

    @PostMapping("/register-ads")
    ApiResponse<AdsCreateResponse> userCreateAds(@RequestPart("request") @Valid AdsCreateRequest request,
                                                 @RequestPart("file") MultipartFile file) {
        var result  = advertisementService.userCreateAds(request, file);
        return ApiResponse.<AdsCreateResponse>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }

    @PutMapping("/approve-ads")
    ApiResponse<AdsApproveResponse> approveAds(@RequestBody AdsApproveRequest request) {
        return ApiResponse.<AdsApproveResponse>builder()
                .code(HttpStatus.OK.value())
                .result(advertisementService.approveAds(request))
                .build();
    }

    @GetMapping("/get-ads-current")
    ApiResponse<PageResponse<AdsCreateResponse>> getAdsByCurrentLogin(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size)
    {
        return ApiResponse.<PageResponse<AdsCreateResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(advertisementService.getAdsByCurrentLogin(page, size))
                .build();
    }

    @GetMapping("/get-ads-active")
    ApiResponse<List<AdsActiveResponse>> getAdsActive () {
        return ApiResponse.<List<AdsActiveResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(advertisementService.getAdsWithActive())
                .build();
    }

}
