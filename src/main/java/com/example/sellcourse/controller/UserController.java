package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.response.otp.VerifyOtpResponse;
import com.example.sellcourse.dto.response.user.*;
import com.example.sellcourse.dto.resquest.auth.ResetPasswordRequest;
import com.example.sellcourse.dto.resquest.email.EmailRequest;
import com.example.sellcourse.dto.resquest.otp.VerifyOtpRequest;
import com.example.sellcourse.dto.resquest.user.ChangePasswordRequest;
import com.example.sellcourse.dto.resquest.user.UserCreateRequest;
import com.example.sellcourse.dto.resquest.user.UserProfileRequest;
import com.example.sellcourse.dto.resquest.user.UserRegisterTeacherRequest;
import com.example.sellcourse.service.CloudinaryService;
import com.example.sellcourse.service.UserService;
import com.nimbusds.jose.shaded.gson.Gson;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    UserService userService;
    CloudinaryService cloudinaryService;

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        var result = userService.getAllUsers();

        return ApiResponse.<List<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }
    @GetMapping("/get-avatar")
    ApiResponse<String> getAvatar(){
        String urlAvatar = cloudinaryService.getAvatar();
        return ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .result(urlAvatar)
                .build();
    }
    @GetMapping("/info-user")
    ApiResponse<UserProfileResponse> getUserProfile(){
        var result = userService.getProfile();

        return ApiResponse.<UserProfileResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Profile info successfully")
                .result(result)
                .build();
    }
    @GetMapping("/registration-teachers")
    ApiResponse<List<UserRegisterTeacherResponse>> getUsersPendingStatus(){
        return ApiResponse.<List<UserRegisterTeacherResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(userService.getUsersPendingStatus())
                .build();
    }
    @GetMapping("/info-teacher/{id}")
    public ApiResponse<InfoTeacherByCourseResponse> getInfoTeacherByCourse(@PathVariable long id){


        return ApiResponse.<InfoTeacherByCourseResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get information teacher successfully")
                .result(userService.getInfoTeacherByCourse(id))
                .build();
    }
    @GetMapping("/get-points-user-current")
    ApiResponse<GetPointsCurrentLogin> getPointsUserLogin(){
        return ApiResponse.<GetPointsCurrentLogin>builder()
                .code(HttpStatus.OK.value())
                .result(userService.getPointsCurrentLogin())
                .build();
    }
    @PostMapping("/update-avatar")
    ApiResponse<String> updateAvatar(@RequestParam("file") MultipartFile file) {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        String url = cloudinaryService.uploadImage(file);

        cloudinaryService.updateAvatar(url, email);

        return ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Profile updated successfully")
                .build();
    }
    @PostMapping(value = "/register-teacher",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserRegisterTeacherResponse> registerTeacher(
            @RequestPart(value = "course")
            @Parameter(
                    description = "The course details in JSON format",
                    schema = @Schema(implementation = UserRegisterTeacherRequest.class)
            ) String requestBodyAsJson,
            @RequestPart("cv") MultipartFile cv,
            @RequestPart("certificate") MultipartFile certificate){
        UserRegisterTeacherRequest request = new Gson().fromJson(requestBodyAsJson, UserRegisterTeacherRequest.class);
        var result = userService.registerTeacher(request, cv, certificate);

        return ApiResponse.<UserRegisterTeacherResponse>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }
    @PostMapping("/register")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request,
                                                @RequestParam String otp) {
        var result = userService.createUser(request, otp);

        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(result)
                .build();
    }
    @PostMapping("/send-otp-forgot-password")
    ApiResponse<Void> sendOtpForgotPassword(@RequestBody EmailRequest request)
            throws MessagingException, UnsupportedEncodingException {

        userService.sendOtpForgotPassword(request);

        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Send Otp Successfully")
                .build();
    }
    @PostMapping("/send-otp-register")
    ApiResponse<Void> sendOtpRegister(@RequestBody EmailRequest request)
            throws MessagingException, UnsupportedEncodingException {
        userService.sendOtpRegister(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Send Otp Successfully")
                .build();
    }
    @PostMapping("/verify-otp")
    public ApiResponse<VerifyOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        var result = userService.verifyOtp(request);

        return ApiResponse.<VerifyOtpResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Verify Otp Successfully")
                .result(result)
                .build();
    }
    @PostMapping("/reset-password")
    ApiResponse<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request ){
        userService.resetPassword(request);

        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Reset Password Successfully")
                .build();
    }
    @PutMapping("/change-password")
    ApiResponse<ChangePasswordResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request){
        return ApiResponse.<ChangePasswordResponse>builder()
                .code(HttpStatus.OK.value())
                .result(userService.changePassword(request))
                .build();
    }
    @PutMapping("/save-teacher/{id}")
    ApiResponse<UserRegisterTeacherResponse> saveTeacher (@PathVariable Long id) {
        var result = userService.approveTeacher(id);

        return ApiResponse.<UserRegisterTeacherResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Approve Teacher Successfully")
                .result(result)
                .build();
    }
    @PutMapping("/reject-teacher/{id}")
    ApiResponse<UserRegisterTeacherResponse> rejectTeacher(@PathVariable Long id) {
        var result = userService.rejectTeacher(id);

        return ApiResponse.<UserRegisterTeacherResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Reject Successfully")
                .result(result)
                .build();
    }
    @PutMapping("/update-profile")
    ApiResponse<Void> updateProfile(@RequestBody UserProfileRequest request){
        userService.updateProfile(request);

        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Profile updated successfully")
                .build();
    }

    @DeleteMapping("remove-avatar")
    ApiResponse<String> removeAvatar() {
        try {
            cloudinaryService.deleteAvatar();
            return ApiResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message("Profile removed successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
        }
    }
}
