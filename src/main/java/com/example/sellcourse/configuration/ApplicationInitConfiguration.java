package com.example.sellcourse.configuration;

import com.example.sellcourse.entities.Role;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.RoleName;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.repository.RoleRepository;
import com.example.sellcourse.repository.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfiguration {

    PasswordEncoder passwordEncoder;
    static final String ADMIN_USER_NAME = "admin@gmail.com";
    static final String ADMIN_PASSWORD = "123456";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "org.postgresql.Driver"
    )
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application.....");

        return args -> {
            Optional<Role> userRole = roleRepository.findByRoleName(RoleName.ROLE_USER);
            if (userRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .roleName(RoleName.ROLE_USER)
                        .build());
            }

            Optional<Role> adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN);
            if (adminRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .roleName(RoleName.ROLE_ADMIN)
                        .build());
            }

            Optional<Role> teacherRole = roleRepository.findByRoleName(RoleName.ROLE_TEACHER);
            if(teacherRole.isEmpty()){
                roleRepository.save(Role.builder()
                        .roleName(RoleName.ROLE_TEACHER)
                        .build());
            }

            if (userRepository.findByEmail(ADMIN_USER_NAME).isEmpty()) {
                Role roleADM = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

                User user = User.builder()
                        .email(ADMIN_USER_NAME)
                        .fullName("Nguyen Minh Hieu")
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .role(roleADM)
                        .dob(LocalDate.of(2002, 4, 15))
                        .build();

                userRepository.save(user);
                log.warn("Admin user has been created with default password: 123456, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}
