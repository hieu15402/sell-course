package com.example.sellcourse.repository;

import com.example.sellcourse.entities.Role;
import com.example.sellcourse.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName name);
}
