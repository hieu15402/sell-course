package com.example.sellcourse.mapper;

import com.example.sellcourse.dto.response.review.ReviewResponse;
import com.example.sellcourse.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "user.fullName", target = "name")
    @Mapping(source = "user.avatar", target = "avatar")
    @Mapping(source = "replies", target = "replies")
    ReviewResponse toCommentResponse(Review comment);

}
