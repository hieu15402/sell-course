package com.example.sellcourse.service;

import com.example.sellcourse.dto.event.CertificateCreateEvent;
import com.example.sellcourse.dto.event.NotificationEvent;
import com.example.sellcourse.dto.response.certificate.CertificateResponse;
import com.example.sellcourse.entities.Certificate;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.repository.CertificateRepository;
import com.example.sellcourse.repository.course.CourseRepository;
import com.example.sellcourse.repository.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KafkaService {
    UserRepository userRepository;
    CourseRepository courseRepository;
    CertificateRepository certificateRepository;
    KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics ="certificate-creation", groupId = "certificate-group")
    public CertificateResponse createCertificate(CertificateCreateEvent creationEvent) {
        log.info("Processing certificate creation for userId: {}, courseId: {}", creationEvent.getUserId(), creationEvent.getCourseId());

        Certificate certificate = Certificate.builder()
                .name("DLearning Certificate of Completion")
                .user(userRepository.findById(creationEvent.getUserId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)))
                .course(courseRepository.findById(creationEvent.getCourseId())
                        .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED)))
                .issueDate(LocalDate.now())
                .build();
        certificateRepository.save(certificate);

        Map<String, Object> data = new HashMap<>();
        data.put("recipient", certificate.getUser().getEmail());
        data.put("courseName", certificate.getCourse().getTitle());
        data.put("issueDate", certificate.getIssueDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy - hh:mm:ss a")));
        data.put("author", certificate.getCourse().getAuthor().getFullName());

        NotificationEvent event =  NotificationEvent.builder()
                .channel("EMAIL")
                .subject("DLearning Certificate of Completion")
                .recipient(certificate.getUser().getEmail())
                .templateCode("certificate-template")
                .param(data)
                .build();

        kafkaTemplate.send("notification-delivery", event);

        return CertificateResponse.builder()
                .certificateId(certificate.getId())
                .email(certificate.getUser().getEmail())
                .courseName(certificate.getCourse().getTitle())
                .author(certificate.getCourse().getAuthor().getFullName())
                .certificateUrl(certificate.getCertificateUrl())
                .issueDate(certificate.getIssueDate())
                .build();
    }
}
