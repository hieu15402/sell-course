package com.example.sellcourse.mapper;

import com.example.sellcourse.dto.response.post.PostCreateResponse;
import com.example.sellcourse.dto.response.post.PostResponse;
import com.example.sellcourse.dto.resquest.post.PostCreateRequest;
import com.example.sellcourse.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    Post toPost(PostCreateRequest request);

    @Mapping(target = "name", source = "user.fullName")
    @Mapping(target = "avatar", source = "user.avatar")
    PostCreateResponse toPostCreateResponse(Post post);

    @Mapping(target = "name", source = "user.fullName")
    @Mapping(target = "avatar", source = "user.avatar")
    @Mapping(target = "likeCount", source = "likeCount")
    PostResponse toPostResponse(Post post);
}
