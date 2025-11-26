package org.clicknshop.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 20)
    private String code;


    @Column(nullable = false)
    private boolean deleted = false;


    @Column(nullable = false)
    private Double discountPercentage;


    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime createdAt;
}