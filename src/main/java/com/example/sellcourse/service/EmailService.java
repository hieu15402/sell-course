package com.example.sellcourse.service;

import com.example.sellcourse.dto.event.NotificationEvent;
import com.example.sellcourse.entities.Advertisement;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    @NonFinal
    @Value("${spring.mail.username}")
    String emailFrom;

    JavaMailSender mailSender;
    SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String subject, String content, List<String> toList) throws MessagingException,
            UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(emailFrom, "Nguyen Minh Hieu");
        helper.setTo(toList.toArray(new String[0]));
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(mimeMessage);
    }

    @KafkaListener(topics = "notification-delivery", groupId = "my-consumer-group")
    public void sendEmailByKafka(NotificationEvent event)
            throws MessagingException, UnsupportedEncodingException {
        log.info("Received Kafka message to send email: {}", event);

        Context context = new Context();
        context.setVariable("recipientName", event.getRecipient());

        if (event.getParam() != null) {
            context.setVariables(event.getParam());
        } else {
            log.warn("Event param is null, cannot set variables in email template.");
        }

        String htmlContent = templateEngine.process(event.getTemplateCode(), context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        helper.setFrom(emailFrom, "DLearning Team");
        helper.setTo(event.getRecipient());
        helper.setSubject(event.getSubject());
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);

        log.info("Email sent to {} successfully!", event.getRecipient());
    }

    public static String getPaymentUrlFromApi(Advertisement advertisement) {
        String apiUrl = "http://localhost:in8080/api/v1/payment/vn-pay?amount=" + advertisement.getPrice() + "&bankCode=NCB";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map<String, Object> result = (Map<String, Object>) response.getBody().get("result");
        return (String) result.get("paymentUrl");
    }

}
