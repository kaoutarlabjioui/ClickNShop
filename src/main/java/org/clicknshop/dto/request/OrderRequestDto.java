package org.clicknshop.dto.request;



import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    @NotNull(message = "clientId requis")
    private Long clientId;

    @NotEmpty(message = "La commande doit contenir au moins un article")
    private List<OrderItemRequestDto> items;


    private String promoCode;
}