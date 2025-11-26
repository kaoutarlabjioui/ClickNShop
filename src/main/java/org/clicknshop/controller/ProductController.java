package org.clicknshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clicknshop.annotation.RequireAuth;
import org.clicknshop.annotation.RequireRole;
import org.clicknshop.dto.request.ProductRequestDto;
import org.clicknshop.dto.response.ProductResponseDto;
import org.clicknshop.model.enums.Role;
import org.clicknshop.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;



    @PostMapping
    @RequireRole({Role.ADMIN})
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto){

        ProductResponseDto response = productService.createProduct(productRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    @RequireRole({Role.ADMIN})
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(  @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "name") String sortBy)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ProductResponseDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @RequireAuth
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }


    @PutMapping("/{id}")
    @RequireRole({Role.ADMIN})
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto productUpdateDto) {
        ProductResponseDto response = productService.updateProduct(id, productUpdateDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/name")
    @RequireAuth
    public ResponseEntity<List<ProductResponseDto>> searchByName(@RequestParam String name) {
        List<ProductResponseDto> products = productService.searchByName(name);
        return ResponseEntity.ok(products);
    }



    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }



}
