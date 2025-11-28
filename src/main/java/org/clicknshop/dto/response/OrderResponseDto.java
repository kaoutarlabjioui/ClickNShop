package org.clicknshop.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long id;
    private LocalDateTime createdAt;
    private Long clientId;
    private List<OrderItemResponseDto> items;
    private BigDecimal subTotalHt;
    private BigDecimal discountAmount;
    private BigDecimal htAfterDiscount;
    private BigDecimal taxAmount;
    private BigDecimal totalTtc;
    private BigDecimal remainingAmount;
    private String status;
    private String promoCode;
}