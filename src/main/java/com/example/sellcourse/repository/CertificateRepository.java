package com.example.sellcourse.repository;

import com.example.sellcourse.entities.Certificate;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    boolean existsByCourseAndUser(Course course, User user);
    List<Certificate> findByUser(User user);

    @Query("select c from Certificate c where c.course.id=:id")
    List<Certificate> getCertificatesByCourseId(Long id);
}
