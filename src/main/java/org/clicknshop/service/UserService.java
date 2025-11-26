package org.clicknshop.service;

import org.clicknshop.dto.request.UserRequestDto;
import org.clicknshop.dto.response.UserRegisterResponseDto;
import org.clicknshop.model.entity.Client;
import org.clicknshop.dto.response.RegisterClientResponseDto;


public interface UserService {

    RegisterClientResponseDto createUserForClient(Client client);
    UserRegisterResponseDto   createUserAdmin(UserRequestDto userRequestDto);

    void deleteUser(Long id);
}
