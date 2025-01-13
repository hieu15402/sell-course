package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.general.PageResponse;
import com.example.sellcourse.dto.response.post.PostCreateResponse;
import com.example.sellcourse.dto.response.post.PostResponse;
import com.example.sellcourse.dto.resquest.post.LikedPostRequest;
import com.example.sellcourse.dto.resquest.post.PostCreateRequest;
import com.example.sellcourse.entities.Post;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.enums.RoleName;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.mapper.PostMapper;
import com.example.sellcourse.repository.PostRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {
    UserRepository userRepository;
    PostRepository postRepository;
    PostMapper postMapper;
    CloudinaryService cloudinaryService;
    @PreAuthorize("isAuthenticated()")
    public PostCreateResponse createPost (PostCreateRequest request, MultipartFile file){

        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        Post post = postMapper.toPost(request);
        post.setUser(user);
        if(file != null){
            String image = cloudinaryService.uploadImage(file);
            post.setImage(image);
        }
        postRepository.save(post);

        return postMapper.toPostCreateResponse(post);
    }

    @PreAuthorize("isAuthenticated()")
    public PageResponse<PostResponse> getPosts (Specification<Post> spec, int page, int size){

        Sort sort = Sort.by(Sort.Direction.DESC,"createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Post> posts = postRepository.findAll(spec, pageable);

        List<PostResponse> postResponses = posts.getContent()
                .stream().map(postMapper::toPostResponse)
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageable.getPageSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .data(postResponses)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    public PageResponse<PostResponse> getOwnPost (int page, int size){
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Sort sort = Sort.by(Sort.Direction.DESC,"createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Post> posts = postRepository.findPostByUser(user, pageable);

        List<PostResponse> postResponse = posts.getContent()
                .stream().map(postMapper::toPostResponse)
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageable.getPageSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .data(postResponse)
                .build();
    }
    @PreAuthorize("isAuthenticated()")
    public void deletePost (Long postId) {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_ID_INVALID));

        if (!Objects.equals(user.getId(), post.getUser().getId()) &&
                !Objects.equals(user.getRole().getRoleName(), RoleName.ROLE_ADMIN)) {
            throw new AppException(ErrorCode.DELETE_POST_INVALID);
        }

        postRepository.delete(post);
    }

    @PreAuthorize("isAuthenticated()")
    public void updateLikeCount(LikedPostRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.ID_POST_INVALID));

        int likeChange = request.getIsLike() ? 1 : -1;
        post.setLikeCount(post.getLikeCount() + likeChange);
        postRepository.save(post);
    }
}
