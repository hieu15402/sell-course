package com.example.sellcourse.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class PaymentMethod extends AbstractEntity<Long> {

    @Column(name = "method_name", nullable = false)
    String methodName;

    @Column(name = "details")
    String details;
}
