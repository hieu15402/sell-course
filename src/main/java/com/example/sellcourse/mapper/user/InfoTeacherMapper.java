package com.example.sellcourse.mapper.user;

import com.example.sellcourse.dto.response.user.InfoTeacherByCourseResponse;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Review;
import com.example.sellcourse.repository.course.CourseRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class InfoTeacherMapper {
    private final CourseRepository courseRepository;
    public InfoTeacherMapper(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    public InfoTeacherByCourseResponse mapToInfoTeacherByCourseResponse(Course course){


        return InfoTeacherByCourseResponse.builder()
                .id(course.getId())
                .author(course.getAuthor().getFullName())
                .rating(getAverageRating(course.getComments()))
                .courseAmount(courseRepository.findByAuthorId(course.getAuthor().getId()).size())
                .reviewAmount(course.getComments().size())
                .description(course.getDescription())
                .build();
    }

    public BigDecimal getAverageRating(List<Review> reviews){
        int sum = reviews.stream().mapToInt(Review::getRating).sum();

        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(reviews.size()), 1, RoundingMode.HALF_UP);
    }
}
