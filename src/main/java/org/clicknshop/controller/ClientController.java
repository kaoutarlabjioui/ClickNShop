package org.clicknshop.controller;

import lombok.RequiredArgsConstructor;
import org.clicknshop.annotation.RequireAuth;

import org.clicknshop.annotation.RequireRole;
import org.clicknshop.dto.response.ClientResponseDto;
import org.clicknshop.dto.response.OrderResponseDto;
import org.clicknshop.mapper.ClientMapper;
import org.clicknshop.model.entity.Client;
import org.clicknshop.model.entity.User;
import org.clicknshop.model.enums.Role;
import org.clicknshop.repository.ClientRepository;

import org.clicknshop.service.ClientService;
import org.clicknshop.service.OrderService;
import org.clicknshop.service.implementation.UserContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final OrderService orderService;
    private final ClientService clientService;


    @GetMapping("/{id}")
    @RequireAuth
    public ResponseEntity<ClientResponseDto> getClient(@PathVariable Long id) {
        ClientResponseDto client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/client-profile")
    @RequireAuth
    public ResponseEntity<ClientResponseDto> getMyProfile() {
        ClientResponseDto dto = clientService.getClientForCurrentUser();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me/orders")
    @RequireAuth
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(Pageable pageable) {
        Page<OrderResponseDto> page = orderService.getOrdersForCurrentUser(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping
    @RequireRole({Role.ADMIN})
    public ResponseEntity<Page<ClientResponseDto>> getAllClients( @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(defaultValue = "name") String sortBy){
        Pageable   pageable = PageRequest.of(page,size, Sort.by(sortBy).ascending());
        Page<ClientResponseDto> clients = clientService.getAllClients(pageable);

        return ResponseEntity.ok(clients);
    }







}