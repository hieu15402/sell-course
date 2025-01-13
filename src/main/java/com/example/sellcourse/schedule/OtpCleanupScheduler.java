package com.example.sellcourse.schedule;

import com.example.sellcourse.entities.User;
import com.example.sellcourse.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OtpCleanupScheduler {

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 1800000) // Chạy mỗi 30 phút
    public void cleanupExpiredOtp() {
        System.out.println("Start Scheduler Delete Otp Expired");

        List<User> usersWithExpiredOtp = userRepository.findAll().stream()
                .filter(user -> user.getOtp() != null && user.getOtpExpiryDate() != null)
                .filter(user -> user.getOtpExpiryDate().isBefore(LocalDateTime.now()))
                .toList();

        if (!usersWithExpiredOtp.isEmpty()) {
            usersWithExpiredOtp.forEach(user -> {
                user.setOtp(null);
                user.setOtpExpiryDate(null);
            });
            userRepository.saveAll(usersWithExpiredOtp);
        }
    }
}
