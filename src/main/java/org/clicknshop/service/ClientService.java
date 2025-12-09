package org.clicknshop.service;

import org.clicknshop.dto.response.ClientResponseDto;
import org.clicknshop.dto.request.UpdateClientRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientService {

    ClientResponseDto updateClient(Long clientId, UpdateClientRequestDto dto);
    Page<ClientResponseDto> getAllClients(Pageable pageable);
    ClientResponseDto getClientById(Long id);
    ClientResponseDto getClientForCurrentUser();
    List<ClientResponseDto> getAllListClients();
    void deleteClient(Long id);
}