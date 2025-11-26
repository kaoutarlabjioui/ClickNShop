package org.clicknshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clicknshop.annotation.RequireAuth;
import org.clicknshop.annotation.RequireRole;
import org.clicknshop.dto.request.UserRequestDto;
import org.clicknshop.dto.response.UserRegisterResponseDto;

import org.clicknshop.model.enums.Role;

import org.clicknshop.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<UserRegisterResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto){

        UserRegisterResponseDto userResponseDto = userService.createUserAdmin(userRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }



    @DeleteMapping("/{userId}")
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}