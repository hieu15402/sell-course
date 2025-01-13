package com.example.sellcourse.mapper;

import com.example.sellcourse.dto.response.comment.CommentResponse;
import com.example.sellcourse.dto.resquest.comment.CommentRequest;
import com.example.sellcourse.dto.resquest.comment.CommentUpdateRequest;
import com.example.sellcourse.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toComment (CommentRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "name", source = "user.fullName")
    @Mapping(target = "avatar", source = "user.avatar")
    @Mapping(target = "replies", source = "replies")
    CommentResponse toCommentResponse (Comment comment);

    void updateComment(CommentUpdateRequest request, @MappingTarget Comment comment);

}
