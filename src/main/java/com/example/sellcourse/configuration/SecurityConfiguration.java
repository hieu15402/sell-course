package com.example.sellcourse.configuration;

import com.example.sellcourse.jwt.CustomJwtDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final CustomJwtDecoder jwtDecoder;
    private static final String[] PUBLIC_ENDPOINT = {
            "/api/v1/auth/**",
            "/api/v1/users/register",
            "/api/v1/auth/logout",
            "/api/v1/auth/introspect",
            "/api/v1/auth/refresh",
            "/api/v1/auth/outbound/authentication",
            "/api/v1/create-password",
            "/api/v1/users/send-otp",
            "/api/v1/users/reset-password",
            "/api/v1/users/verify-otp",
            "/api/v1/users/check-exists-user",
            "/api/v1/users/send-otp-register",
            "/api/v1/courses/**",
            "/ws/**",
            "/upload/**",
            "/api/v1/courses-review/{courseId}",
            "/api/v1/payment/vn-pay-callback",
            "/api/v1/payment/vn-pay/**",
            "/api/v1/get-ads-active",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml/**",
            "/v3/api-docs/public",
            "/swagger-ui/**",
            "/swagger-ui/index.html"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(PUBLIC_ENDPOINT).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(jwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return httpSecurity.build();
    }

    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
