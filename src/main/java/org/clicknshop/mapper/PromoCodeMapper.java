package org.clicknshop.mapper;

import org.clicknshop.dto.request.PromoCodeRequestDto;
import org.clicknshop.dto.response.PromoCodeResponseDto;
import org.clicknshop.model.entity.PromoCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromoCodeMapper {


    PromoCodeResponseDto toDto(PromoCode entity);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    PromoCode toEntity(PromoCodeRequestDto dto);


}
