package com.example.sellcourse.repository;

import com.example.sellcourse.entities.Advertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Query("SELECT ads FROM Advertisement ads WHERE ads.user.id = :userId ORDER BY ads.createdAt DESC")
    Page<Advertisement> findAdvertisementByUserId(Long userId, Pageable pageable);

    @Query("SELECT a FROM Advertisement a WHERE a.approvalStatus = :status")
    List<Advertisement> findAdvertisementByApprovalStatusActive(@Param("status") String status);


}
