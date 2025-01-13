package com.example.sellcourse.service.admin;

import com.example.sellcourse.dto.response.user.Admin_TeacherResponse;
import com.example.sellcourse.dto.response.user.Admin_UserResponse;
import com.example.sellcourse.entities.Role;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.RegistrationStatus;
import com.example.sellcourse.enums.RoleName;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.user.Admin_TeacherMapper;
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
public class Admin_TeacherService {

    private final UserRepository userRepository;
    private final Admin_TeacherMapper teacherMapper;
    private final RoleRepository roleRepository;
    private final Admin_UserMapper userMapper;

    public Page<Admin_TeacherResponse> getTeachers(Pageable pageable) {
        return userRepository.findByRoleName(RoleName.ROLE_TEACHER.toString(), pageable)
                .map(teacherMapper::toTeacherResponse);
    }

    public Page<Admin_TeacherResponse> searchTeachersByKeywords(String[] keywords, Pageable pageable) {
        return userRepository.searchByRoleAndMultipleKeywords(RoleName.ROLE_TEACHER.toString(), keywords, pageable)
                .map(teacherMapper::toTeacherResponse);
    }

    @Transactional
    public void approveTeacherRegistration(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getRegistrationStatus() != RegistrationStatus.PENDING) {
            throw new AppException(ErrorCode.REGISTRATION_NOT_PENDING);
        }

        Role teacherRole = roleRepository.findByRoleName(RoleName.ROLE_TEACHER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        user.setRole(teacherRole);
        user.setRegistrationStatus(RegistrationStatus.APPROVED);
        userRepository.save(user);
    }

    @Transactional
    public void rejectTeacherRegistration(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); // Sử dụng ErrorCode

        if (user.getRegistrationStatus() != RegistrationStatus.PENDING) {
            throw new AppException(ErrorCode.REGISTRATION_NOT_PENDING); // Sử dụng ErrorCode
        }

        user.setRegistrationStatus(RegistrationStatus.REJECTED);
        userRepository.save(user);
    }

    @Transactional
    public void removeTeacherRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        user.setRole(userRole);
        user.setRegistrationStatus(null);
        userRepository.save(user);
    }

    public Page<Admin_UserResponse> getPendingTeacherApplications(Pageable pageable) {
        return userRepository.findByRoleNameAndRegistrationStatus(RoleName.ROLE_USER.toString(),
                        RegistrationStatus.PENDING, pageable)
                .map(userMapper::toUserResponse);
    }


}
