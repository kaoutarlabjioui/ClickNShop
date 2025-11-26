package org.clicknshop.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCodeResponseDto {
    private Long id;
    private String code;
    private Double discountPercentage;
    private Boolean active;
    private Boolean deleted;
    private LocalDateTime createdAt;
}