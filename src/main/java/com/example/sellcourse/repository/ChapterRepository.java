package com.example.sellcourse.repository;

import com.example.sellcourse.entities.Chapter;
import com.example.sellcourse.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Optional<Chapter> findByChapterNameAndCourse(String chapterName, Course course);
}
