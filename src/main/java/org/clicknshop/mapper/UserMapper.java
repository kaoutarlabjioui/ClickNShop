package org.clicknshop.mapper;


import org.clicknshop.dto.request.RegisterRequestDto;
import org.clicknshop.dto.response.UserResponseDto;
import org.clicknshop.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User entity);
    User toEntity(RegisterRequestDto dto);

}