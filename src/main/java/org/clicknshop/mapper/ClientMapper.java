package org.clicknshop.mapper;


import org.clicknshop.dto.request.RegisterClientRequestDto;
import org.clicknshop.dto.response.ClientResponseDto;
import org.clicknshop.model.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ClientMapper {


    @Mapping(source = "user", target = "user")
    ClientResponseDto toDto(Client entity);

    List<ClientResponseDto> toDto(List<Client> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "totalOrders", constant = "0")
    @Mapping(target = "totalSpent", expression = "java(java.math.BigDecimal.ZERO)")
    Client toEntity(RegisterClientRequestDto dto);
}