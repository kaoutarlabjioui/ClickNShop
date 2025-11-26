
package org.clicknshop.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterClientResponseDto {
    private Long clientId;
    private String username;
    private String temporaryPassword;
}