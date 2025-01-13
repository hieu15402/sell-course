package com.example.sellcourse.controller;

import com.example.sellcourse.dto.response.chapter.ChapterCreateResponse;
import com.example.sellcourse.dto.response.general.ApiResponse;
import com.example.sellcourse.dto.resquest.chapter.ChapterCreateRequest;
import com.example.sellcourse.service.ChapterService;
import com.nimbusds.jose.shaded.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/chapters")
@Slf4j
public class ChapterController {

    ChapterService chapterService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload chapter with JSON and file",
            description = "Uploads chapter details (JSON) and the associated lesson file."
    )
    ApiResponse<ChapterCreateResponse> createLesson(
            @RequestPart(value = "chapter")
            @Parameter(
                    description = "The chapter details in JSON format, including chapter name, description, and other metadata.",
                    schema = @Schema(implementation = ChapterCreateRequest.class)
            ) String requestBodyAsJson,
            @RequestPart("file") MultipartFile file) throws IOException {

        ChapterCreateRequest request = new Gson().fromJson(requestBodyAsJson, ChapterCreateRequest.class);
        return ApiResponse.<ChapterCreateResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(chapterService.createChapter(request, file))
                .build();
    }

}
