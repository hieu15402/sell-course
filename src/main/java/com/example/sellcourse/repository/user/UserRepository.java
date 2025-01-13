package com.example.sellcourse.repository.user;

import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);


    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName AND " +
            "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchByRoleAndKeywords(@Param("roleName") String roleName,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);


    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName AND u.registrationStatus = :status")
    Page<User> findByRoleNameAndRegistrationStatus(@Param("roleName") String roleName,
                                                   @Param("status") RegistrationStatus status,
                                                   Pageable pageable);
}
