package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.auth.AuthenticationResponse;
import com.example.sellcourse.dto.response.auth.IntrospectResponse;
import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.resquest.auth.AuthenticationRequest;
import com.example.sellcourse.dto.resquest.auth.IntrospectRequest;
import com.example.sellcourse.dto.resquest.auth.LogoutRequest;
import com.example.sellcourse.dto.resquest.auth.RefreshTokenRequest;
import com.example.sellcourse.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {

        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) {

        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Logout Successfully")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }

}
