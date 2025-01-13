package com.example.sellcourse.schedule;

import com.example.sellcourse.entities.Advertisement;
import com.example.sellcourse.enums.AdsStatus;
import com.example.sellcourse.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdsCleanUpScheduler {
    private final AdvertisementRepository adRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanAds() {
        List<Advertisement> ads = adRepository.findAll();
        if(!ads.isEmpty()) {
            ads.stream()
                    .filter(a ->  a.getApprovalStatus().equals(AdsStatus.ACTIVE) && a.getEndDate().isBefore(LocalDate.now()))
                    .forEach(a -> {
                        a.setApprovalStatus(AdsStatus.COMPLETED);
                        adRepository.save(a);
                        log.info("Ad ID {} has been updated to COMPLETED.", a.getId());
                    });
        }
    }
}
