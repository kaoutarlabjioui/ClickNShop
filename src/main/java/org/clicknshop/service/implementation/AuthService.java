package org.clicknshop.service.implementation;

import lombok.RequiredArgsConstructor;
import org.clicknshop.dto.request.LoginRequestDto;
import org.clicknshop.dto.request.RegisterClientRequestDto;
import org.clicknshop.dto.response.AuthResponseDto;
import org.clicknshop.dto.response.RegisterClientResponseDto;
import org.clicknshop.dto.response.UserResponseDto;
import org.clicknshop.model.entity.Client;
import org.clicknshop.model.entity.User;
import org.clicknshop.model.enums.CustomerTier;
import org.clicknshop.mapper.UserMapper;
import org.clicknshop.repository.ClientRepository;
import org.clicknshop.repository.UserRepository;
import org.clicknshop.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    private final ClientRepository clientRepository;
    private final SessionManager sessionManager;



    @Transactional
    public AuthResponseDto login(LoginRequestDto req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides"));

        if (!BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Identifiants invalides");
        }

        String sessionId = sessionManager.createSession(user);
        UserResponseDto uDto = userMapper.toDto(user);
        return AuthResponseDto.builder()
                .message("Connexion réussie")
                .user(uDto)
                .sessionId(sessionId)
                .build();
    }

    public void logout(String sessionId) {
        sessionManager.invalidateSession(sessionId);
    }

    @Transactional
    public RegisterClientResponseDto registerClientByAdmin(RegisterClientRequestDto dto) {

        if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un client existe déjà avec cet email");
        }


        Client client = Client.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .createdAt(LocalDateTime.now())
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .loyaltyLevel(CustomerTier.BASIC)
                .build();
        client = clientRepository.save(client);

        RegisterClientResponseDto userResp = userService.createUserForClient(client);


        return userResp;
    }
}