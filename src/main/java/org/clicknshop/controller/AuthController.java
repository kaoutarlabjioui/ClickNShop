package org.clicknshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clicknshop.annotation.RequireAuth;
import org.clicknshop.annotation.RequireRole;
import org.clicknshop.dto.request.LoginRequestDto;
import org.clicknshop.dto.request.RegisterClientRequestDto;
import org.clicknshop.dto.response.AuthResponseDto;
import org.clicknshop.dto.response.RegisterClientResponseDto;
import org.clicknshop.dto.response.UserResponseDto;
import org.clicknshop.mapper.UserMapper;
import org.clicknshop.model.entity.User;
import org.clicknshop.model.enums.Role;
import org.clicknshop.service.implementation.AuthService;
import org.clicknshop.service.implementation.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserContext userContext;
    private final UserMapper userMapper;


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto req) {
        AuthResponseDto resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    @RequireAuth
    public ResponseEntity<Void> logout(@RequestHeader("Session-Id") String sessionId) {
        authService.logout(sessionId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/register-client")
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<RegisterClientResponseDto> registerClient(@Valid @RequestBody RegisterClientRequestDto dto) {
        RegisterClientResponseDto resp = authService.registerClientByAdmin(dto);
        return ResponseEntity.ok(resp);
    }


    @GetMapping("/me")
    @RequireAuth
    public ResponseEntity<UserResponseDto> me() {
        User current = userContext.getCurrentUser();
        if (current == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userMapper.toDto(current));
    }
}