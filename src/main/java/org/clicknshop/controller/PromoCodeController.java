package org.clicknshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clicknshop.annotation.RequireAuth;
import org.clicknshop.annotation.RequireRole;

import org.clicknshop.dto.request.PromoCodeRequestDto;
import org.clicknshop.dto.response.PromoCodeResponseDto;
import org.clicknshop.model.enums.Role;
import org.clicknshop.service.PromoCodeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promocodes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @GetMapping
    @RequireAuth
    public ResponseEntity<Page<PromoCodeResponseDto>> list(Pageable pageable) {
        return ResponseEntity.ok(promoCodeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @RequireAuth
    public ResponseEntity<PromoCodeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(promoCodeService.getById(id));
    }

    @PostMapping
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<PromoCodeResponseDto> create(@Valid @RequestBody PromoCodeRequestDto dto) {
        return ResponseEntity.ok(promoCodeService.createCodePromo(dto));
    }

    @PutMapping("/{id}")
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<PromoCodeResponseDto> update(@PathVariable Long id, @Valid @RequestBody PromoCodeRequestDto dto) {
        return ResponseEntity.ok(promoCodeService.updateCodePromo(id, dto));
    }

    @DeleteMapping("/{id}")
    @RequireAuth
    @RequireRole({Role.ADMIN})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        promoCodeService.delete(id);
        return ResponseEntity.noContent().build();
    }


}