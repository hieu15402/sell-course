package com.example.sellcourse.repository;

import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Enrollment;
import com.example.sellcourse.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserAndCourse(User user, Course course);
    List<Enrollment> findByUser(User user);
    Optional<Enrollment> findByCourseAndUser(Course course, User user);
}
