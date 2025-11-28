package org.clicknshop.dto.request;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemRequestDto {
    @NotNull(message = "productId requis")
    private Long productId;

    @NotNull(message = "quantity requis")
    @Min(value = 1, message = "quantity doit être au moins 1")
    private Integer quantity;
}