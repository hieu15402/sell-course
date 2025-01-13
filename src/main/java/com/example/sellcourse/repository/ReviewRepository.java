package com.example.sellcourse.repository;

import com.example.sellcourse.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.course.id = :courseId AND r.chapter IS NULL AND r.lesson IS NULL")
    List<Review> findByCourseIdAndChapterIsNullAndLessonIsNull(Long courseId);

}
