package com.example.sellcourse.entities;

import com.example.sellcourse.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role extends AbstractEntity<Long> {

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    RoleName roleName;

    @OneToMany(mappedBy = "role")
    Set<User> users;
}
