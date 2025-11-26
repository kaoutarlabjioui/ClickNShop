package org.clicknshop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class RegisterClientRequestDto {
    @NotBlank(message = "Nom du client requis")
    private String name;

    @NotBlank(message = "Email requis")
    @Email(message = "Email invalide")
    private String email;

    private String username;
}