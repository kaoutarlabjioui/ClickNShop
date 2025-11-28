package org.clicknshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clicknshop.annotation.RequireAuth;
import org.clicknshop.dto.request.PaymentRequestDto;
import org.clicknshop.dto.response.PaymentResponseDto;
import org.clicknshop.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{orderId}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @RequireAuth
    public ResponseEntity<PaymentResponseDto> addPayment(@PathVariable Long orderId, @Valid @RequestBody PaymentRequestDto dto) {
        PaymentResponseDto resp = paymentService.addPayment(orderId, dto);
        return ResponseEntity.status(201).body(resp);
    }
}