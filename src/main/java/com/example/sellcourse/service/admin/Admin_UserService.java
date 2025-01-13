package com.example.sellcourse.service.admin;

import com.example.sellcourse.dto.response.user.Admin_UserResponse;
import com.example.sellcourse.dto.response.user.TeacherApplicationDetailResponse;
import com.example.sellcourse.entities.Role;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.RoleName;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.user.Admin_UserMapper;
import com.example.sellcourse.repository.RoleRepository;
import com.example.sellcourse.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Admin_UserService {

    private final UserRepository userRepository;
    private final Admin_UserMapper userMapper;
    private final RoleRepository roleRepository;


    public Page<Admin_UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    public Page<Admin_UserResponse> searchUsersByKeywords(String[] keywords, Pageable pageable) {
        return userRepository.searchByMultipleKeywords(keywords, pageable)
                .map(userMapper::toUserResponse);
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getEnabled() != null && !user.getEnabled()) {
            throw new AppException(ErrorCode.USER_ALREADY_BANNED);
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getEnabled() != null && user.getEnabled()) {
            throw new AppException(ErrorCode.USER_NOT_BANNED);
        }

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(Long userId, RoleName roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Role newRole = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        user.setRole(newRole); // Update user role
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public TeacherApplicationDetailResponse getUserApplicationDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return TeacherApplicationDetailResponse.builder()
                .id(user.getId())
                .name(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender() != null ? user.getGender() : null)
                .avatar(user.getAvatar())
                .dob(user.getDob()) // Giữ nguyên LocalDate
                .cvUrl(user.getCvUrl())
                .certificate(user.getCertificate())
                .facebookLink(user.getFacebookLink())
                .description(user.getDescription())
                .yearsOfExperience(user.getYearsOfExperience())
                .points(user.getPoints())
                .role(user.getRole().getRoleName())
                .build();
    }


}
