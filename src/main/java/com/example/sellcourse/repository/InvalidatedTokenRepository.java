package com.example.sellcourse.repository;

import com.example.sellcourse.entities.InvalidatedToken;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    boolean existsById(@NonNull String id);
}
