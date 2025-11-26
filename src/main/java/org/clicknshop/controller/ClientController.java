package org.clicknshop.controller;

import lombok.RequiredArgsConstructor;
import org.clicknshop.annotation.RequireAuth;

import org.clicknshop.dto.response.ClientResponseDto;
import org.clicknshop.mapper.ClientMapper;
import org.clicknshop.model.entity.Client;
import org.clicknshop.model.entity.User;
import org.clicknshop.model.enums.Role;
import org.clicknshop.repository.ClientRepository;

import org.clicknshop.service.implementation.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UserContext userContext;


    @GetMapping("/{id}")
    @RequireAuth
    public ResponseEntity<ClientResponseDto> getClient(@PathVariable Long id) {
        User current = userContext.getCurrentUser();
        if (current == null) return ResponseEntity.status(401).build();


        if (current.getRole() == Role.CLIENT) {

            var maybeClient = clientRepository.findByUserId(current.getId());
            if (maybeClient.isEmpty() || !maybeClient.get().getId().equals(id)) {
                return ResponseEntity.status(403).build();
            }
        }

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable"));
        return ResponseEntity.ok(clientMapper.toDto(client));
    }
}