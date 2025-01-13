package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.revenue.RevenueDetailResponse;
import com.example.sellcourse.dto.response.revenue.RevenueResponse;
import com.example.sellcourse.dto.response.revenue.RevenueSummaryResponse;
import com.example.sellcourse.dto.resquest.revenue.PeriodTypeRequest;
import com.example.sellcourse.dto.resquest.revenue.RevenueRequest;
import com.example.sellcourse.entities.Payment;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.PaymentStatus;
import com.example.sellcourse.enums.PeriodType;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.repository.PaymentRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RevenueService {
    PaymentRepository paymentRepository;
    UserRepository userRepository;

    @PreAuthorize("isAuthenticated() && hasAnyAuthority('ADMIN', 'TEACHER')")
    public RevenueResponse totalRevenue (RevenueRequest request) {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        if(request.getStartDate() != null && request.getEndDate() != null) {
            start = request.getStartDate().atStartOfDay();
            end = request.getEndDate().atTime(LocalTime.MAX);
        } else {
            start = switch (request.getPeriodType()) {
                case DAY -> end.toLocalDate().atStartOfDay();
                case WEEK -> end.minusDays(7);
                case MONTH -> end.minusMonths(1);
                case YEAR -> end.minusYears(1);
            };
        }
        List<Payment> payments = paymentRepository.findByUserAndDateRange(user, start, end);

        return RevenueResponse.builder()
                .startDate(start.toLocalDate())
                .endDate(end.toLocalDate())
                .periodType(request.getPeriodType())
                .totalRevenue(payments.stream().map(Payment::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }

    @PreAuthorize("isAuthenticated() && hasAnyAuthority('ADMIN', 'TEACHER')")
    public List<RevenueDetailResponse> totalRevenueDetail (PeriodTypeRequest request) {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getPeriodType() == null || request.getYear() == null) {
            return Collections.emptyList();
        }
        List<RevenueDetailResponse> revenueDetails = new ArrayList<>();
        int year = request.getYear();
        if(request.getPeriodType().equals(PeriodType.YEAR) && request.getMonth() == null) {

            for(int month  = 1; month <= 12; month ++) {
                LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
                LocalDateTime end = start.plusMonths(1).minusDays(1);

                List<Payment> payments = paymentRepository.findByUserAndDateRange(user, start, end);
                BigDecimal monthRevenue = payments.stream()
                        .map(Payment::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                RevenueDetailResponse detailMonth = RevenueDetailResponse.builder()
                        .month(month)
                        .year(year)
                        .revenue(monthRevenue)
                        .build();
                revenueDetails.add(detailMonth);
            }
        } else if (request.getPeriodType().equals(PeriodType.YEAR)) {
            int month = request.getMonth();
            LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endOfMonth = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()) // xác định ngày cuối cùng trong tháng
                    .toLocalDate().atTime(23, 59, 59); // tinh toan ngày và giờ cuối cùng trong tháng(23h:59:59 ngày cuối cùng trong tháng)

            int totalWeek = (int) Math.ceil((double) endOfMonth.getDayOfMonth() / 7);
            for(int week = 1; week <= totalWeek; week ++) {
                LocalDateTime end = start.plusWeeks(1).minusSeconds(1);
                if (end.isAfter(endOfMonth)) {
                    endOfMonth = end; // Tuần cuối không vượt quá ngày cuối của tháng
                }
                List<Payment> payments = paymentRepository.findByUserAndDateRange(user, start, end);

                BigDecimal weekRevenue = payments.stream().map(Payment::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                RevenueDetailResponse detailWeek = RevenueDetailResponse.builder()
                        .week(week)
                        .month(month)
                        .year(year)
                        .revenue(weekRevenue)
                        .build();

                revenueDetails.add(detailWeek);
                start = end.plusSeconds(1);
            }
        }

        return revenueDetails;
    }

    @PreAuthorize("isAuthenticated() && hasAnyAuthority('ADMIN', 'TEACHER')")
    public RevenueSummaryResponse revenueTeacher (PeriodTypeRequest request) {
        var email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<RevenueDetailResponse> revenueDetails = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        if(request.getPeriodType().equals(PeriodType.YEAR) && request.getYear() != null && request.getMonth() == null) {
            int year = request.getYear();
            for(int month = 1; month <= 12; month ++) {
                LocalDateTime start = LocalDateTime.of(year, month, 1,0, 0);
                LocalDateTime end = start.plusMonths(1).minusDays(1);

                List<Payment> payments = paymentRepository
                        .findPaymentsByAuthorStatusAndDateRange(user, PaymentStatus.COMPLETED, start, end);

                BigDecimal monthRevenue = payments.stream().map(Payment::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                totalRevenue = totalRevenue.add(monthRevenue);
                RevenueDetailResponse detailMonth = RevenueDetailResponse.builder()
                        .month(month)
                        .year(year)
                        .revenue(monthRevenue)
                        .build();
                revenueDetails.add(detailMonth);
            }
        } else if (request.getPeriodType().equals(PeriodType.YEAR) && request.getYear() != null) {
            int year = request.getYear();
            int month = request.getMonth();

            LocalDateTime start = LocalDateTime.of(year, month, 1,0, 0);
            LocalDateTime endOfMonth = start.withDayOfMonth(start.toLocalDate().lengthOfMonth())
                    .toLocalDate().atTime(23,59,59);

            int totalWeek = (int) Math.ceil((double) endOfMonth.getDayOfMonth() / 7);
            for(int week = 1; week <= totalWeek; week ++) {
                LocalDateTime end = start.plusWeeks(1).minusSeconds(1);
                if(end.isAfter(endOfMonth)) {
                    end = endOfMonth;
                }

                List<Payment> payments = paymentRepository
                        .findPaymentsByAuthorStatusAndDateRange(user, PaymentStatus.COMPLETED, start, end);

                BigDecimal weekRevenue = payments.stream().map(Payment::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                totalRevenue = totalRevenue.add(weekRevenue);

                RevenueDetailResponse detailMonth = RevenueDetailResponse.builder()
                        .month(month)
                        .year(year)
                        .week(week)
                        .revenue(weekRevenue)
                        .build();
                revenueDetails.add(detailMonth);
                start = end.plusSeconds(1);
            }
        }
        return RevenueSummaryResponse.builder()
                .totalRevenue(totalRevenue)
                .revenueDetails(revenueDetails)
                .build();
    }
}
