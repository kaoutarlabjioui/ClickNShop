package org.clicknshop.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
 public class UserRegisterResponseDto {

    private String username;

    private String password;


 }
