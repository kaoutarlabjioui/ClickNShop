package org.clicknshop.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.clicknshop.dto.request.PromoCodeRequestDto;
import org.clicknshop.dto.response.PromoCodeResponseDto;

import org.clicknshop.exception.DuplicateResourceException;
import org.clicknshop.exception.ResourceNotFoundException;
import org.clicknshop.mapper.PromoCodeMapper;
import org.clicknshop.model.entity.PromoCode;
import org.clicknshop.repository.PromoCodeRepository;
import org.clicknshop.service.PromoCodeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromoCodeServiceImp implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;

    @Override
    @Transactional
    public PromoCodeResponseDto createCodePromo(PromoCodeRequestDto dto) {
        String code = dto.getCode().toUpperCase();
        if (promoCodeRepository.existsByCode(code)) {
            throw new DuplicateResourceException("Un code promo existe déjà avec ce code");
        }

        PromoCode entity = promoCodeMapper.toEntity(dto);
        entity.setCode(code);
        promoCodeRepository.save(entity);
        return promoCodeMapper.toDto(entity);
    }

    @Override
    @Transactional
    public PromoCodeResponseDto updateCodePromo(Long id, PromoCodeRequestDto dto) {
        PromoCode existing = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Code promo introuvable"));


        String newCode = dto.getCode().toUpperCase();
        if (!existing.getCode().equals(newCode)) {
            if (promoCodeRepository.existsByCode(newCode)) {
                throw new DuplicateResourceException("Un code promo existe déjà avec ce code");
            }
            existing.setCode(newCode);
        }

        existing.setDiscountPercentage(dto.getDiscountPercentage());
        existing.setActive(dto.getActive() == null ? existing.isActive() : dto.getActive());

        promoCodeRepository.save(existing);
        return promoCodeMapper.toDto(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public PromoCodeResponseDto getById(Long id) {
        PromoCode p = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Code promo introuvable"));
        return promoCodeMapper.toDto(p);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromoCodeResponseDto> findAll(Pageable pageable) {
        Page<PromoCode> page = promoCodeRepository.findByDeletedFalse(pageable);
        return page.map(promoCodeMapper::toDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PromoCode p = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Code promo introuvable"));
        p.setDeleted(true);
        p.setActive(false);
        promoCodeRepository.save(p);
    }

}