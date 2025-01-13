package com.example.sellcourse.mapper;

import com.example.sellcourse.dto.response.favorite.FavoriteResponse;
import com.example.sellcourse.entities.Favorite;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public FavoriteResponse toFavoriteResponse(Favorite favorite){
        return FavoriteResponse.builder()
                .name(favorite.getUser().getFullName())
                .favoriteId(favorite.getId())
                .courseId(favorite.getCourse().getId())
                .title(favorite.getCourse().getTitle())
                .thumbnail(favorite.getCourse().getThumbnail())
                .points(favorite.getCourse().getPoints())
                .author(favorite.getCourse().getAuthor().getFullName())
                .build();
    }
}
