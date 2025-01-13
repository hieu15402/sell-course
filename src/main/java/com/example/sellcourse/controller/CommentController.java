package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.comment.CommentResponse;
import com.example.sellcourse.dto.response.comment.CommentUpdateResponse;
import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.response.general.PageResponse;
import com.example.sellcourse.dto.resquest.comment.CommentRequest;
import com.example.sellcourse.dto.resquest.comment.CommentUpdateRequest;
import com.example.sellcourse.service.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/comments")
@Slf4j
public class CommentController {
    CommentService commentService;

    @GetMapping("/post-comment/{postId}")
    ApiResponse<PageResponse<CommentResponse>> findAll(
            @PathVariable Long postId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "3") int size) {

        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(commentService.getCommentByPostId(postId, page, size))
                .build();
    }

    @PostMapping
    ApiResponse<CommentResponse> addComment (@RequestBody @Valid CommentRequest request){
        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.OK.value())
                .result(commentService.addComment(request))
                .build();
    }

    @DeleteMapping("/{commentId}")
    ApiResponse<Void> deleteComment (@PathVariable Long commentId){
        commentService.deleteComment(commentId);

        return ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Delete Comment Successfully")
                .build();
    }

    @PutMapping("{commentId}")
    ApiResponse<CommentUpdateResponse> updateComment (@PathVariable Long commentId, @RequestBody @Valid CommentUpdateRequest request) {
        return ApiResponse.<CommentUpdateResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Update Comment Successfully")
                .result(commentService.updateComment(commentId, request))
                .build();
    }
}
