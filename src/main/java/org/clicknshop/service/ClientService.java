package org.clicknshop.service;

import org.clicknshop.dto.response.ClientResponseDto;
import org.clicknshop.dto.request.UpdateClientRequestDto;

public interface ClientService {

    ClientResponseDto updateClient(Long clientId, UpdateClientRequestDto dto);
    void deleteClient(Long id);
}