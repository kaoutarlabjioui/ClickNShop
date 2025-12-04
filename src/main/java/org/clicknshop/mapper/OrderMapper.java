package org.clicknshop.mapper;

import org.clicknshop.dto.request.OrderRequestDto;
import org.clicknshop.dto.response.OrderResponseDto;
import org.clicknshop.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "promoCode.code", target = "promoCode")
    @Mapping(source = "status", target = "status")
    OrderResponseDto toDto(Order entity);
    @Mapping(target = "promoCode", ignore = true)
    Order toEntity(OrderRequestDto orderRequestDto);
}