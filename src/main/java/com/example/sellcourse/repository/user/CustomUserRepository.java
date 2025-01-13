package com.example.sellcourse.repository.user;

import com.example.sellcourse.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserRepository {
    Page<User> searchByMultipleKeywords(String[] keywords, Pageable pageable);
    Page<User> searchByRoleAndMultipleKeywords(String roleName, String[] keywords, Pageable pageable); // Thêm phương thức mới

}
