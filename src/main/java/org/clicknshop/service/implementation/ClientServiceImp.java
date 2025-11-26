package org.clicknshop.service.implementation;

import lombok.RequiredArgsConstructor;
import org.clicknshop.dto.response.ClientResponseDto;
import org.clicknshop.dto.request.UpdateClientRequestDto;
import org.clicknshop.exception.DuplicateResourceException;
import org.clicknshop.exception.ResourceNotFoundException;
import org.clicknshop.model.entity.Client;
import org.clicknshop.model.entity.User;
import org.clicknshop.model.enums.Role;
import org.clicknshop.mapper.ClientMapper;
import org.clicknshop.repository.ClientRepository;
import org.clicknshop.repository.UserRepository;
import org.clicknshop.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImp implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;

    @Override
    @Transactional
    public ClientResponseDto updateClient(Long clientId, UpdateClientRequestDto dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));


        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(client.getEmail())) {
            if (clientRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("Un client existe déjà avec cet email");
            }
            client.setEmail(dto.getEmail());
        }


        if (dto.getName() != null) {
            client.setName(dto.getName());
        }


        if (dto.getUserId() != null) {
            Long newUserId = dto.getUserId();

            if (newUserId == 0L) {

                client.setUser(null);
            } else {
                User user = userRepository.findById(newUserId)
                        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable pour l'id fourni"));


                if (user.getRole() != Role.CLIENT) {
                    throw new IllegalArgumentException("L'utilisateur doit avoir le rôle CLIENT pour être associé");
                }


                Optional<Client> existing = clientRepository.findByUserId(newUserId);
                if (existing.isPresent() && !existing.get().getId().equals(clientId)) {
                    throw new DuplicateResourceException("Cet utilisateur est déjà associé à un autre client");
                }

                client.setUser(user);
            }
        }


        Client saved = clientRepository.save(client);


        return clientMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteClient(Long id){
        Client client = clientRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("client introuvable"));

        clientRepository.delete(client);
    }



}