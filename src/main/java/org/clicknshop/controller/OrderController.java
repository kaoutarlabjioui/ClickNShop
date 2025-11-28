package org.clicknshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clicknshop.annotation.RequireAuth;
import org.clicknshop.annotation.RequireRole;
import org.clicknshop.dto.request.OrderRequestDto;
import org.clicknshop.dto.response.OrderResponseDto;
import org.clicknshop.model.enums.Role;
import org.clicknshop.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @RequireAuth
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto dto) {
        OrderResponseDto resp = orderService.createOrder(dto);
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/{id}")
    @RequireAuth
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/client/{clientId}")
    @RequireAuth
    public ResponseEntity<Page<OrderResponseDto>> listByClient(@PathVariable Long clientId, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByClient(clientId, pageable));
    }

    @PostMapping("/{id}/confirm")
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<OrderResponseDto> confirmOrder(@PathVariable Long id) {
        OrderResponseDto dto = orderService.confirmOrder(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/cancel")
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long id) {
        OrderResponseDto dto = orderService.cancelOrder(id);
        return ResponseEntity.ok(dto);
    }
}
