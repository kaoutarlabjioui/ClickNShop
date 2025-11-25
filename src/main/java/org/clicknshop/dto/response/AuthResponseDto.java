package org.clicknshop.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private String message;
    private UserResponseDto user;
    private String sessionId;
}