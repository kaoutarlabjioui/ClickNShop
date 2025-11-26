package org.clicknshop.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;


@Data
public class UpdateClientRequestDto {
    private String name;

    @Email(message = "Email invalide")
    private String email;

    private Long userId;
}