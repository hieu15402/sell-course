package com.example.sellcourse.repository;

import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Favorite;
import com.example.sellcourse.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Page<Favorite> findByUser(User user, Pageable pageable);

    List<Favorite> findByUser(User user);

    boolean existsByUserAndCourse(User user, Course course);
}
