package com.example.sellcourse.service;

import com.example.sellcourse.dto.event.NotificationEvent;
import com.example.sellcourse.dto.response.otp.VerifyOtpResponse;
import com.example.sellcourse.dto.response.user.*;
import com.example.sellcourse.dto.resquest.auth.ResetPasswordRequest;
import com.example.sellcourse.dto.resquest.email.EmailRequest;
import com.example.sellcourse.dto.resquest.otp.VerifyOtpRequest;
import com.example.sellcourse.dto.resquest.user.ChangePasswordRequest;
import com.example.sellcourse.dto.resquest.user.UserCreateRequest;
import com.example.sellcourse.dto.resquest.user.UserProfileRequest;
import com.example.sellcourse.dto.resquest.user.UserRegisterTeacherRequest;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Role;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.RegistrationStatus;
import com.example.sellcourse.enums.RoleName;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.user.InfoTeacherMapper;
import com.example.sellcourse.mapper.user.ProfileMapper;
import com.example.sellcourse.mapper.user.RegisterTeacherMapper;
import com.example.sellcourse.mapper.user.UserMapper;
import com.example.sellcourse.repository.RoleRepository;
import com.example.sellcourse.repository.course.CourseRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    ProfileMapper profileMapper;
    RegisterTeacherMapper registerTeacherMapper;
    RoleRepository roleRepository;
    NotificationService notificationService;
    CloudinaryService cloudinaryService;
    CourseRepository courseRepository;
    InfoTeacherMapper infoTeacherMapper;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    OtpService otpService;
    KafkaTemplate<String, Object> kafkaTemplate;
    EmailService emailService;

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updateProfile(UserProfileRequest request) {
        SecurityContext contextHolder = SecurityContextHolder.getContext();
        String email = contextHolder.getAuthentication().getName();

        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            profileMapper.updateUser(request, user);

            if (request.getFirstName() != null && request.getLastName() != null) {
                user.setFullName(request.getFirstName() + " " + request.getLastName());
            }
            userRepository.save(user);
            log.info("User profile updated successfully for user with email: {}", email);
        }
    }
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ChangePasswordResponse changePassword(ChangePasswordRequest request){
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        if(! passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new AppException(ErrorCode.CURRENT_PASSWORD_INVALID);

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_EXISTED);
        }

        if(! Objects.equals(request.getNewPassword(), request.getConfirmPassword()))
            throw new AppException(ErrorCode.CONFIRM_PASSWORD_INVALID);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        return ChangePasswordResponse
                .builder()
                .message("Change password successful")
                .success(true)
                .build();
    }
    @PreAuthorize("isAuthenticated()")
    public UserProfileResponse getProfile(){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return profileMapper.getInfoUser(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserRegisterTeacherResponse> getUsersPendingStatus() {
        return userRepository.findAll()
                .stream()
                .filter(user ->
                        user.getRegistrationStatus() != null &&
                                user.getRegistrationStatus().equals(RegistrationStatus.PENDING) &&
                                user.getRole() != null &&
                                user.getRole().getRoleName() != null &&
                                user.getRole().getRoleName().equals(RoleName.ROLE_USER))
                .map(registerTeacherMapper::toTeacherResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAuthority('USER') and isAuthenticated()")
    public UserRegisterTeacherResponse registerTeacher(UserRegisterTeacherRequest request,
                                                       MultipartFile cv,
                                                       MultipartFile certificate){

        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User userCurrent = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (userCurrent.getRegistrationStatus() == null ||
                userCurrent.getRegistrationStatus().equals(RegistrationStatus.REJECTED)) {

            String cvUrl = cloudinaryService.uploadImage(cv);
            String certificateUrl = cloudinaryService.uploadImage(certificate);

            request.setCvUrl(cvUrl);
            request.setCertificate(certificateUrl);

            registerTeacherMapper.toUpdateTeacher(request, userCurrent);
            userCurrent.setRegistrationStatus(RegistrationStatus.PENDING);
            userRepository.save(userCurrent);

            String message = "A new teacher application has been submitted.";
            String title = "New Teacher Registration";
            String url = "/admin/teacher-applications";

            List<User> userAdmin = userRepository.findByRoleName(RoleName.ROLE_ADMIN.toString());
            for (User usersAdmin : userAdmin) {
                notificationService.createNotification(usersAdmin, userCurrent, message, title, url);
            }

            return registerTeacherMapper.toTeacherResponse(userCurrent);
        }
        throw new AppException(ErrorCode.REGISTER_TEACHER_INVALID);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') and isAuthenticated()")
    public UserRegisterTeacherResponse approveTeacher(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String roleName = user.getRole().getRoleName().toString();

        Role role = roleRepository.findByRoleName(RoleName.ROLE_TEACHER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        if(user.getRegistrationStatus().equals(RegistrationStatus.PENDING)
                && roleName.equals(RoleName.ROLE_USER.toString())){
            user.setRegistrationStatus(RegistrationStatus.APPROVED);
            user.setRole(role);
            userRepository.save(user);

            String message = "Your application to become a teacher has been approved.";
            String title = "Teacher Registration Approved";
            String url = "/teacher";

            List<User> userAdmin = userRepository.findByRoleName(RoleName.ROLE_ADMIN.toString());
            for (User usersAdmin : userAdmin){
                notificationService.createNotification(user, usersAdmin, message, title, url);
            }
            return registerTeacherMapper.toTeacherResponse(user);
        }
        throw new AppException(ErrorCode.REGISTER_TEACHER_INVALID);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') and isAuthenticated()")
    public UserRegisterTeacherResponse rejectTeacher(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String roleName = user.getRole().getRoleName().toString();

        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));


        if(user.getRegistrationStatus().equals(RegistrationStatus.PENDING)
                && roleName.equals(RoleName.ROLE_USER.toString())){
            user.setRegistrationStatus(RegistrationStatus.REJECTED);
            user.setRole(role);
            user.setBio(null);
            user.setCertificate(null);
            user.setCvUrl(null);
            user.setFacebookLink(null);
            userRepository.save(user);
            log.info("RegistrationStatus {}", user.getRegistrationStatus());

            String message = "Your application to become a teacher has been rejected.";
            String title = "Teacher Registration Rejected";
            String url = "/support";

            List<User> userAdmin = userRepository.findByRoleName(RoleName.ROLE_ADMIN.toString());
            for (User usersAdmin : userAdmin){
                notificationService.createNotification(user, usersAdmin, message, title, url);
            }
        }
        return registerTeacherMapper.toTeacherResponse(user);
    }

    public InfoTeacherByCourseResponse getInfoTeacherByCourse(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COURSER_NOT_EXISTED));

        return infoTeacherMapper.mapToInfoTeacherByCourseResponse(course);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request, String otp)  {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        String storedOtp = otpService.getOtp(request.getEmail());
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        User user = userMapper.toUser(request);

        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        userRepository.save(user);

        otpService.deleteOtp(request.getEmail());

        NotificationEvent event = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(user.getEmail())
                .templateCode("welcome-email")
                .subject("Welcome to DLearning")
                .build();
        kafkaTemplate.send("notification-delivery", event);

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll()
                .stream().map(userMapper::toUserResponse).toList();
    }
    @Transactional
    public void sendOtpForgotPassword(EmailRequest request)
            throws MessagingException, UnsupportedEncodingException {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String otp = generateOtp();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

        user.setOtp(otp);
        user.setOtpExpiryDate(expiryDate);

        String subject = "Your OTP Code";
        String content = String.format(
                "<p>Hello,</p>" +
                        "<p>We received a request to reset your password. Use the following OTP to reset it:</p>" +
                        "<h2>%s</h2>" +
                        "<p>If you did not request this, please ignore this email.</p>" +
                        "<p>Best regards,<br/>Your Company</p>",
                otp
        );
        emailService.sendEmail(subject, content, List.of(user.getEmail()));
    }
    public void sendOtpRegister(EmailRequest request)
            throws MessagingException, UnsupportedEncodingException {
        String otp = generateOtp();

        otpService.saveOtp(request.getEmail(), otp);

        String subject = "Your OTP Code for Account Registration";

        String emailContent = "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                "<h2 style='color: #4CAF50;'>Welcome to DLearning!</h2>" +
                "<p>Dear <strong>" +
                request.getEmail() +
                "</strong>,</p>" +
                "<p>Thank you for registering with <strong>DLearning</strong>. We are excited to have you on board!</p>" +
                "<p style='font-size: 18px;'><strong>Your OTP Code is:</strong> " +
                "<span style='font-size: 22px; color: #FF5733;'><strong>" +
                otp +
                "</strong></span></p>" +
                "<p><strong>Note:</strong> This OTP is valid for <em>5 minutes</em>. Please enter it as soon as possible to complete your registration.</p>" +
                "<p>If you did not request this code, please ignore this email. For your security, do not share this code with anyone.</p>" +
                "<br/>" +
                "<p>Best regards,</p>" +
                "<p><strong>DLearning Team</strong></p>" +
                "</body>" +
                "</html>";
        emailService.sendEmail(subject, emailContent, List.of(request.getEmail()));
    }
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(user.getOtp() == null || ! user.getOtp().equals(request.getOtp())){
            return VerifyOtpResponse.builder()
                    .valid(false)
                    .build();
        }

        if(user.getOtpExpiryDate() == null || user.getOtpExpiryDate().isBefore(LocalDateTime.now())){
            return VerifyOtpResponse.builder()
                    .valid(false)
                    .build();
        }

        return VerifyOtpResponse.builder()
                .valid(true)
                .build();
    }
    @Transactional
    public void resetPassword(ResetPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        if (user.getOtpExpiryDate() == null || user.getOtpExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOtp(null);
        user.setOtpExpiryDate(null);
        userRepository.save(user);
    }
    @PreAuthorize("isAuthenticated()")
    public GetPointsCurrentLogin getPointsCurrentLogin(){
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return GetPointsCurrentLogin.builder()
                .points(user.getPoints())
                .build();
    }
    private static String generateOtp(){

        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for(int i = 0; i < 6 ; i++){
            otp.append(random.nextInt(10));
        }
        return otp.toString();

    }
}
