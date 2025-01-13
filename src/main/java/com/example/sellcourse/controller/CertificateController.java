package com.example.sellcourse.controller;

import com.example.sellcourse.dto.event.CertificateCreateEvent;
import com.example.sellcourse.dto.response.certificate.CertificateResponse;
import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.service.CertificateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/certificates")
@Slf4j
public class CertificateController {

    CertificateService certificateService;

    @PostMapping("/creation")
    ApiResponse<Void> createCertification (@RequestBody CertificateCreateEvent request) {
        certificateService.createCertificate(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Certificate created")
                .build();
    }

    @GetMapping("/current-login")
    ApiResponse<List<CertificateResponse>> getCertificationByUserLogin() {
        return ApiResponse.<List<CertificateResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(certificateService.getCertificationByUserLogin())
                .build();
    }

}
