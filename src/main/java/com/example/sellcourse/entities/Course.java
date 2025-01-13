package com.example.sellcourse.entities;

import com.example.sellcourse.enums.CourseLevel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "wishlists"})
public class Course  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "point", columnDefinition = "BIGINT DEFAULT 0")
    Long points;

    @Column(name = "duration")
    Integer duration; // in hours

    @Column(name = "language")
    String language;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    CourseLevel courseLevel;

    @Column(name = "thumbnail")
    String thumbnail;

    @Column(name = "video_url")
    String videoUrl;

    @Column(name = "price")
    BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonBackReference
    User author;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "course"})
    Set<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Review> comments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Favorite> favorites;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    Set<Chapter> chapters;

    @Column(name = "created_by")
    @CreatedBy
    String createdBy;

    @Column(name = "updated_by")
    @LastModifiedBy
    String updateBy;

    @Column(name = "create_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @Column(name = "enabled", nullable = false)
    Boolean enabled;
}
