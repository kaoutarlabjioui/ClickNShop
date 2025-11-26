package org.clicknshop.service;

import org.clicknshop.dto.request.ProductRequestDto;
import org.clicknshop.dto.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto productRequestDto);
    Page<ProductResponseDto> getAllProducts(Pageable pageable);
    ProductResponseDto getProductById(Long id);
    ProductResponseDto updateProduct(Long id , ProductRequestDto productRequestDto);
    void deleteProduct(Long id );
    boolean existsById(Long id);
    List<ProductResponseDto> searchByName(String name);



}
