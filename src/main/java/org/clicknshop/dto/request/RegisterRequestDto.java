package org.clicknshop.dto.request;
import lombok.Data;

/**
 * Minimal fields used to create a User + Client (if used).
 * When admin registers clients, use client's email as username.
 */
@Data
public class RegisterRequestDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}