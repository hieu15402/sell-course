package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.response.general.PageResponse;
import com.example.sellcourse.dto.response.post.PostCreateResponse;
import com.example.sellcourse.dto.response.post.PostResponse;
import com.example.sellcourse.dto.resquest.post.LikedPostRequest;
import com.example.sellcourse.dto.resquest.post.PostCreateRequest;
import com.example.sellcourse.entities.Post;
import com.example.sellcourse.service.PostService;
import com.nimbusds.jose.shaded.gson.Gson;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {
    PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<PostCreateResponse> createPost (
            @RequestPart("request")
            @Parameter(
                    description = "The post details in JSON format, including content.",
                    schema = @Schema(implementation = PostCreateRequest.class)
            ) String requestBodyAsJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        PostCreateRequest request = new Gson().fromJson(requestBodyAsJson, PostCreateRequest.class);
        return ApiResponse.<PostCreateResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(postService.createPost(request, file))
                .build();
    }

    @DeleteMapping("/{postId}")
    ApiResponse<Void> deletePost (@PathVariable Long postId) {
        postService.deletePost(postId);

        return ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Delete post successfully")
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<PostResponse>> getPosts (
            @Filter Specification<Post> spec,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "3") int size){

        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(postService.getPosts(spec, page, size))
                .build();
    }

    @GetMapping("/own-posts")
    ApiResponse<PageResponse<PostResponse>> getPostByCurrentLogin(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size
    ){
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(postService.getOwnPost(page, size))
                .build();
    }

    @PutMapping("/update-like-count")
    ApiResponse<Void> updateLikeCount(@RequestBody LikedPostRequest request) {
        postService.updateLikeCount(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Update like successfully")
                .build();
    }
}
