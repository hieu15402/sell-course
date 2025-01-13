package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.review.ReviewDeleteResponse;
import com.example.sellcourse.dto.response.review.ReviewResponse;
import com.example.sellcourse.dto.response.review.ReviewUpdateResponse;
import com.example.sellcourse.dto.resquest.review.ReviewRequest;
import com.example.sellcourse.dto.resquest.review.ReviewUpdateRequest;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Review;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.ReviewMapper;
import com.example.sellcourse.repository.ReviewRepository;
import com.example.sellcourse.repository.course.CourseRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewService {
    UserRepository userRepository;
    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;
    CourseRepository courseRepository;
    BannedWordsService bannedWordsService;

    public List<ReviewResponse> getReviewByCourse(Long id) {
        List<Review> allReviews = reviewRepository.findByCourseIdAndChapterIsNullAndLessonIsNull(id);
        return allReviews.stream()
                .filter(comment -> comment.getParentReview() == null)
                .map(reviewMapper::toCommentResponse)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ReviewResponse addReview(ReviewRequest reviewRequest, Long courseId) {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSER_NOT_EXISTED));

        Review parentReview = null;
        if (reviewRequest.getParentReviewId() != null) {
            parentReview = reviewRepository.findById(reviewRequest.getParentReviewId())
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_COMMENT_NOT_EXISTED));
        }

        if ((reviewRequest.getContent() == null || reviewRequest.getContent().isEmpty()) && reviewRequest.getRating() == null) {
            throw new AppException(ErrorCode.INVALID_COMMENT_OR_RATING);
        }

        if (reviewRequest.getRating() != null && (reviewRequest.getRating() < 0 || reviewRequest.getRating() > 5)) {
            throw new AppException(ErrorCode.INVALID_RATING);
        }

        if ( reviewRequest.getContent()!= null && bannedWordsService.containsBannedWords(reviewRequest.getContent())) {
            throw new AppException(ErrorCode.INVALID_COMMENT_CONTENT);
        }

        Review newComment = Review.builder()
                .user(user)
                .content(reviewRequest.getContent() != null && !reviewRequest.getContent().isEmpty()
                        ? reviewRequest.getContent()
                        : "")
                .rating(reviewRequest.getRating())
                .course(course)
                .parentReview(parentReview)
                .build();

        reviewRepository.save(newComment);

        return reviewMapper.toCommentResponse(newComment);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ReviewDeleteResponse deleteReviewById(Long id) {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Review comment = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        if (Objects.equals(user.getId(), comment.getUser().getId())) {
            reviewRepository.deleteById(id);
            return ReviewDeleteResponse.builder()
                    .id(id)
                    .message("Delete Comment Successfully")
                    .build();
        }

        throw new AppException(ErrorCode.DELETE_COMMENT_INVALID);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ReviewUpdateResponse updateReview(Long id, ReviewUpdateRequest request) {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Review comment = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        if (Objects.equals(user.getId(), comment.getUser().getId())) {
            if (request.getContent() != null && bannedWordsService.containsBannedWords(request.getContent())) {
                throw new AppException(ErrorCode.INVALID_COMMENT_CONTENT);
            }

            if (request.getContent() != null) {
                comment.setContent(request.getContent());
            }
            reviewRepository.save(comment);

            return ReviewUpdateResponse.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .build();
        }

        throw new AppException(ErrorCode.UPDATE_COMMENT_INVALID);
    }
}
