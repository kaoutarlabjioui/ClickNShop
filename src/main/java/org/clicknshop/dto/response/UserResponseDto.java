package org.clicknshop.dto.response;

import lombok.*;
import org.clicknshop.model.enums.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private Role role;
}