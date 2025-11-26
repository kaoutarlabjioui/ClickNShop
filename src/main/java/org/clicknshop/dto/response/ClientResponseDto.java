package org.clicknshop.dto.response;

import lombok.*;
import org.clicknshop.model.enums.CustomerTier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDto {
    private Long id;
    private String name;
    private String email;
    private CustomerTier loyaltyLevel;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDate firstOrderDate;
    private LocalDate lastOrderDate;
    private LocalDateTime createdAt;
    private UserResponseDto user;
}