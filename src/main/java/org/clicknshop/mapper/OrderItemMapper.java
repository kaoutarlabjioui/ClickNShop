package org.clicknshop.mapper;

import org.clicknshop.dto.request.OrderItemRequestDto;
import org.clicknshop.dto.response.OrderItemResponseDto;
import org.clicknshop.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId", defaultValue = "0")
    @Mapping(source = "product.name", target = "productName", defaultValue = "")
    OrderItemResponseDto toDto(OrderItem entity);

    OrderItem toEntity(OrderItemRequestDto orderItemRequestDto);
}