package com.example.sellcourse.schedule;

import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.RoleName;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendEmailScheduler {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 0 ? * 7")
    public void sendWeeklyEmail() throws MessagingException, UnsupportedEncodingException {
        log.info("Sending weekly email");
        List<User> users = userRepository.findAll()
                .stream().filter(u -> u.getRole().getRoleName().equals(RoleName.ROLE_USER))
                .toList();

        if(!users.isEmpty()) {
            String subject = "A Warm Thank You from DLearning";
            String content = """
        <h1>Hello,</h1>
        <p>Thank you for being a valued part of our community. We truly appreciate your trust and commitment to learning with us.</p>
        <p>As the week comes to a close, we wanted to take a moment to express our gratitude and let you know that we're here to support you every step of the way.</p>
        <p>If you have any feedback or need assistance, please don't hesitate to reach out. Your success is our top priority, and we're always happy to hear from you.</p>
        <p>Best wishes for a productive and fulfilling week ahead!</p>
        <p>Warm regards,<br>The DLearning Team</p>""";
            List<String> emailList  = users.stream()
                    .map(User::getEmail)
                    .toList();
            emailService.sendEmail(subject, content, emailList );
        }
    }
}
