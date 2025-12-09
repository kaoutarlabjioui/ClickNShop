package org.clicknshop.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clicknshop.dto.response.ClientResponseDto;
import org.clicknshop.dto.request.UpdateClientRequestDto;
import org.clicknshop.dto.response.OrderResponseDto;
import org.clicknshop.exception.DuplicateResourceException;
import org.clicknshop.exception.ResourceNotFoundException;
import org.clicknshop.model.entity.Client;
import org.clicknshop.model.entity.User;
import org.clicknshop.model.enums.Role;
import org.clicknshop.mapper.ClientMapper;
import org.clicknshop.repository.ClientRepository;
import org.clicknshop.repository.OrderRepository;
import org.clicknshop.repository.UserRepository;
import org.clicknshop.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImp implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final  UserContext userContext;


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
    public ClientResponseDto getClientById(Long id){

        Client client = clientRepository.findById(id) .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));
        return clientMapper.toDto(client);
    }

    @Override
    public ClientResponseDto getClientForCurrentUser() {
        Client client = resolveClientFromContext();
        return clientMapper.toDto(client);
    }


    @Override
    public Page<ClientResponseDto> getAllClients(Pageable pageable){
        log.info("Récupération de tous les clients");

        Page<Client> clients = clientRepository.findAll(pageable);

        return clients.map(clientMapper::toDto);


    }

    @Override
    @Transactional
    public void deleteClient(Long id){
        Client client = clientRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("client introuvable"));

        clientRepository.delete(client);
    }

    private Client resolveClientFromContext() {

        User current = userContext.getCurrentUser();
        if (current == null) {
            log.warn("Utilisateur non authentifié (UserContext vide)");
            throw new ResourceNotFoundException("Utilisateur non authentifié");
        }

        Long userId = current.getId();
        return clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Client lié à l'utilisateur introuvable"));
    }
    @Override
    public List<ClientResponseDto> getAllListClients(){
        log.info("Récupération de tous les clients");

        List<Client> clients = clientRepository.findAll();

        return clientMapper.toDto(clients);

    }



}