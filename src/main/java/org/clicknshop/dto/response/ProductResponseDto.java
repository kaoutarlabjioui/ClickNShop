package org.clicknshop.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private Long id;

    private String name;

    private BigDecimal unitPrice;

    private Integer availableStock;

}
