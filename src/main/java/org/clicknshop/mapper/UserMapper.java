package org.clicknshop.mapper;



import org.clicknshop.dto.request.UserRequestDto;
import org.clicknshop.dto.response.UserRegisterResponseDto;
import org.clicknshop.dto.response.UserResponseDto;
import org.clicknshop.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);
    UserRegisterResponseDto toUserRegisterDto(User user);
    User toEntity(UserRequestDto dto);

}