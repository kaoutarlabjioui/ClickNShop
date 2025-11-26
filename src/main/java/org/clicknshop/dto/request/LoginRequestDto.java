package org.clicknshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "username requis")
    private String username;

    @NotBlank(message = "password requis")
    private String password;
}