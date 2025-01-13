package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.favorite.FavoriteResponse;
import com.example.sellcourse.dto.response.general.PageResponse;
import com.example.sellcourse.dto.resquest.favorite.FavoriteRequest;
import com.example.sellcourse.entities.Course;
import com.example.sellcourse.entities.Favorite;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.FavoriteMapper;
import com.example.sellcourse.repository.FavoriteRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FavoriteService {
    FavoriteRepository favoriteRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    FavoriteMapper favoriteMapper;

    @PreAuthorize("isAuthenticated()")
    public void createFavorite (FavoriteRequest request) {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Course course = courseRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSER_NOT_EXISTED));

        boolean isAlreadyFavorite = favoriteRepository.existsByUserAndCourse(user, course);
        if (isAlreadyFavorite) {
            throw new AppException(ErrorCode.ALREADY_IN_FAVORITES);
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .course(course)
                .build();

        favoriteRepository.save(favorite);
    }

    public Favorite findById(Integer id) {
        return favoriteRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FAVORITE_NOT_EXISTED));
    }

    @PreAuthorize("isAuthenticated()")
    public PageResponse<FavoriteResponse> findByCurrentUser(int page, int size) {

        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Favorite> favorites = favoriteRepository.findByUser(user, pageable);

        return PageResponse.<FavoriteResponse>builder()
                .currentPage(page)
                .pageSize(favorites.getSize())
                .totalPages(favorites.getTotalPages())
                .totalElements(favorites.getTotalElements())
                .data(favorites.getContent().stream().map(favoriteMapper::toFavoriteResponse).toList())
                .build();
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void deleteFavorite(Integer favoriteId){
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Favorite> favorites = favoriteRepository.findByUser(user);

        Favorite favoriteToDelete = favorites.stream()
                .filter(f -> Objects.equals(f.getId(), favoriteId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favoriteToDelete);
    }
}
