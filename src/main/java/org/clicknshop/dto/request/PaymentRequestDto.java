package org.clicknshop.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRequestDto {

    @NotNull
    @DecimalMin(value = "0.01", message = "Montant doit être >= 0.01")
    private BigDecimal amount;

    @NotNull
    @Size(min = 1)
    private String paymentType;


    private LocalDateTime depositDate;

    private String reference;
    private String bank;
}