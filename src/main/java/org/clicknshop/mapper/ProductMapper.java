package org.clicknshop.mapper;

import org.clicknshop.dto.request.ProductRequestDto;
import org.clicknshop.dto.response.ProductResponseDto;
import org.clicknshop.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring" )
public interface ProductMapper {

    Product toEntity(ProductRequestDto productRequestDto);
    ProductResponseDto toResponseDto(Product product);
    void updateEntityFromDto(ProductRequestDto productRequestDto, @MappingTarget Product product);

    List<ProductResponseDto> toResponseDtoList(List<Product> products);

}
