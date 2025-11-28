package org.clicknshop.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
    private Long id;
    private Integer paymentNumber;
    private BigDecimal amount;
    private String paymentType;
    private LocalDateTime paymentDate;
    private LocalDateTime depositDate;
    private String status;
    private String reference;
    private String bank;
}