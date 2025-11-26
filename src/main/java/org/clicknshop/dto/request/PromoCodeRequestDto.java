package org.clicknshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PromoCodeRequestDto {

    @NotBlank(message = "Le code est requis")
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Le code doit respecter le format PROMO-XXXX (A-Z0-9)")
    private String code;

    @NotNull(message = "Le pourcentage de réduction est requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le pourcentage doit être > 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Le pourcentage doit être <= 100")
    private Double discountPercentage;


    private Boolean active = true;

}