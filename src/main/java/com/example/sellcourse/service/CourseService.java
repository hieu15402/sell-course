package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.chapter.ChapterResponse;
import com.example.sellcourse.dto.response.course.CourseChapterResponse;
import com.example.sellcourse.dto.response.course.CourseResponse;
import com.example.sellcourse.dto.response.course.UploadCourseResponse;
import com.example.sellcourse.dto.response.general.PageResponse;
import com.example.sellcourse.dto.resquest.course.UploadCourseRequest;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Review;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.course.CourseChapterAndLessonMapper;
import com.example.sellcourse.mapper.course.CourseMapper;
import com.example.sellcourse.repository.course.CourseRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseService {
    UserRepository userRepository;
    CourseRepository courseRepository;
    CloudinaryService cloudinaryService;
    CourseMapper courseMapper;
    CourseChapterAndLessonMapper courseChapterAndLessonMapper;


    public PageResponse<CourseResponse> getCourses(Specification<Course> spec, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Course> pageData = courseRepository.findAll(spec, pageable);

        List<CourseResponse> courseResponses  = pageData.getContent()
                .stream().map(course -> {

                    List<Review> filteredComments = course.getComments().stream()
                            .filter(r -> r.getRating() > 0 )
                            .toList();

                    long totalRating = filteredComments.stream()
                            .mapToLong(Review::getRating)
                            .sum();

                    int numberOfValidReviews = filteredComments.size();
                    double averageRating = numberOfValidReviews > 0 ? BigDecimal.valueOf((double) totalRating / numberOfValidReviews)
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue() : 0.0 ;

                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);
                    courseResponse.setAverageRating(averageRating);
                    return courseResponse;
                }).toList();

        return PageResponse.<CourseResponse>builder()
                .currentPage(page)
                .pageSize(pageable.getPageSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(courseResponses)
                .build();
    }

    public List<String> getTitleSuggestions(String query) {
        return courseRepository.findTitleSuggestions(query);
    }

    public CourseResponse getCourseById(Long id){

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSER_NOT_EXISTED));

        return CourseResponse.builder()
                .id(course.getId())
                .author(course.getAuthor().getFullName())
                .title(course.getTitle())
                .description(course.getDescription())
                .duration(course.getDuration())
                .language(course.getLanguage())
                .courseLevel(course.getCourseLevel())
                .thumbnail(course.getThumbnail())
                .videoUrl(course.getVideoUrl())
                .points(course.getPoints())
                .build();
    }

    @PreAuthorize("isAuthenticated() and hasAnyAuthority('ROLE_USER', 'ROLE_TEACHER', 'ROLE_ADMIN')")
    public List<CourseResponse> getOwnCourses(){
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Course> myCourse = courseRepository.findByAuthorId(user.getId());

        return myCourse.stream().map(courseMapper::toCourseResponse).toList();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('ROLE_ADMIN', 'ROLE_TEACHER')")
    public UploadCourseResponse uploadCourse(UploadCourseRequest request, MultipartFile file, MultipartFile thumbnail)
            throws IOException {

        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String videoUrl = cloudinaryService.uploadVideoChunked(file, "courses").get("url").toString();
        String thumbnailUrl = cloudinaryService.uploadImage(thumbnail);

        Course course = courseMapper.updateCourse(request);
        course.setVideoUrl(videoUrl);
        course.setThumbnail(thumbnailUrl);
        course.setAuthor(user);
        course.setEnabled(true);

        courseRepository.save(course);

        return courseMapper.toUploadCourseResponse(course);
    }

    public CourseChapterResponse getAllInfoCourse (Long courseId){

        courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSER_NOT_EXISTED));

        CourseChapterResponse courseLessonResponse =  courseChapterAndLessonMapper
                .getCourseLessonAndLessonContent(courseId);

        Set<ChapterResponse> sortedChapter = courseLessonResponse.getChapters().stream()
                .sorted(Comparator.comparing(ChapterResponse::getChapterId))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        courseLessonResponse.setChapters(sortedChapter);

        return courseLessonResponse;
    }
}
