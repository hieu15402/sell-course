package com.example.sellcourse.entities;

import com.example.sellcourse.enums.RegistrationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AbstractEntity<Long>{
    @Column(name = "email", nullable = false, unique = true)
    @NotNull
    @Email
    String email;

    @Column(name = "password")
    String password;

    @Column(name = "name", nullable = false)
    String fullName;

    @Column(name = "avatar")
    String avatar;

    @Column(name = "gender")
    String gender;

    @Column(name = "phone")
    String phone;

    @Column(name = "dob")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    LocalDate dob;

    @Column(name = "otp")
    String otp;

    @Column(name = "otp_expiry_date")
    LocalDateTime otpExpiryDate;

    @Column(name = "address")
    String address;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "zipCode")
    String zipCode;

    @Column(name = "enabled")
    Boolean enabled;

    @Column(name = "expertise")
    String expertise;

    @Column(name = "yearsOfExperience")
    Double yearsOfExperience;

    @Column(name = "bio")
    String bio;

    @Column(name = "certificate")
    String certificate;

    @Column(name = "cvUrl")
    String cvUrl;

    @Column(name = "facebookLink")
    String facebookLink;

    @Column(name = "points", columnDefinition = "BIGINT DEFAULT 0")
    Long points;

    @Column(name = "registrationStatus")
    @Enumerated(EnumType.STRING)
    RegistrationStatus registrationStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
    Set<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
    Set<Enrollment> enrollments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
    Set<Favorite> favorites;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
    List<Post> posts;

    @PrePersist
    protected void onCreate() {
        if (enabled == null) {
            enabled = Boolean.TRUE;
        }
    }
}
