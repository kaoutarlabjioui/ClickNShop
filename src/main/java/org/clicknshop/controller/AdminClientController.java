package org.clicknshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clicknshop.annotation.RequireAuth;
import org.clicknshop.annotation.RequireRole;
import org.clicknshop.dto.request.UpdateClientRequestDto;
import org.clicknshop.dto.response.ClientResponseDto;
import org.clicknshop.model.enums.Role;
import org.clicknshop.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/admin/clients")
@RequiredArgsConstructor
public class AdminClientController {

    private final ClientService clientService;



    @PutMapping("/{id}")
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClientRequestDto dto) {

        ClientResponseDto updated = clientService.updateClient(id, dto);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {

        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}