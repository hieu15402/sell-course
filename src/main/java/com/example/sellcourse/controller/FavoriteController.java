package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.favorite.FavoriteResponse;
import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.response.general.PageResponse;
import com.example.sellcourse.dto.resquest.favorite.FavoriteRequest;
import com.example.sellcourse.entities.Favorite;
import com.example.sellcourse.service.FavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/favorites")
@Slf4j
public class FavoriteController {

    FavoriteService favoriteService;

    @PostMapping
    ApiResponse<Void> saveFavorite (@RequestBody FavoriteRequest request) {
        favoriteService.createFavorite(request);

        return ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Save Favorite Successfully")
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<Favorite> getFavorite (@PathVariable Integer id) {
        var result = favoriteService.findById(id);

        return ApiResponse.<Favorite>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }

    @DeleteMapping("/{favoriteId}")
    ApiResponse<Void> deleteFavorite (@PathVariable Integer favoriteId){
        favoriteService.deleteFavorite(favoriteId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Delete Favorite Successfully")
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<FavoriteResponse>> fetchAllFavorite (@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = "6")  int size) {
        var result = favoriteService.findByCurrentUser(page, size);

        return ApiResponse.<PageResponse<FavoriteResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .build();
    }

}
