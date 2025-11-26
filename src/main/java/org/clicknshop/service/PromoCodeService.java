package org.clicknshop.service;

import org.clicknshop.dto.request.PromoCodeRequestDto;
import org.clicknshop.dto.response.PromoCodeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromoCodeService {

    PromoCodeResponseDto createCodePromo(PromoCodeRequestDto dto);
    PromoCodeResponseDto updateCodePromo(Long id, PromoCodeRequestDto dto);
    PromoCodeResponseDto getById(Long id);
    Page<PromoCodeResponseDto> findAll(Pageable pageable);
    void delete(Long id);
}
