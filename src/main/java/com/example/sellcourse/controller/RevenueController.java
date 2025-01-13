package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.response.revenue.RevenueDetailResponse;
import com.example.sellcourse.dto.response.revenue.RevenueResponse;
import com.example.sellcourse.dto.response.revenue.RevenueSummaryResponse;
import com.example.sellcourse.dto.resquest.revenue.PeriodTypeRequest;
import com.example.sellcourse.dto.resquest.revenue.RevenueRequest;
import com.example.sellcourse.service.RevenueService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/revenues")
public class RevenueController {

    RevenueService revenueService;

    @GetMapping("/revenue")
    ApiResponse<RevenueResponse> revenue (@RequestBody RevenueRequest request) {
        return ApiResponse.<RevenueResponse>builder()
                .code(HttpStatus.OK.value())
                .result(revenueService.totalRevenue(request))
                .build();
    }

    @PostMapping("/revenue-detail")
    ApiResponse<List<RevenueDetailResponse>> revenueDetail (@RequestBody PeriodTypeRequest request) {
        return ApiResponse.<List<RevenueDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(revenueService.totalRevenueDetail(request))
                .build();
    }

    @PostMapping("/teacher/revenue-detail")
    ApiResponse<RevenueSummaryResponse> revenueDetailTeacher (@RequestBody PeriodTypeRequest request) {
        return ApiResponse.<RevenueSummaryResponse>builder()
                .code(HttpStatus.OK.value())
                .result(revenueService.revenueTeacher(request))
                .build();
    }
}
