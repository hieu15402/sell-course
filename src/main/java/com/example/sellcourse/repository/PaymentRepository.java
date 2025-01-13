package com.example.sellcourse.repository;

import com.example.sellcourse.entities.Payment;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.user.id = :id ORDER BY p.createdAt DESC ")
    Page<Payment> transactionHistory(Long id, Pageable pageable);
    Optional<Payment> findByTransactionId(String transactionId);

    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.createdAt BETWEEN :start AND :end")
    List<Payment> findByUserAndDateRange(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.status = :status AND p.createdAt BETWEEN :start AND :end")
    List<Payment> findPaymentsByAuthorStatusAndDateRange(
            @Param("user") User user,
            @Param("status") PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
