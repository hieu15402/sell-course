package com.example.sellcourse.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "lessons")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson extends AbstractEntity<Long> {

    @Column(name = "lesson_name")
    String lessonName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chapter_id", nullable = false)
    Chapter chapter;

    @Column(name = "content_type")
    String contentType; // "video", "document", etc.

    @Column(name = "content_url")
    String videoUrl;

    @Column(name = "description")
    String description; // Mô tả nội dung nếu cần
}
