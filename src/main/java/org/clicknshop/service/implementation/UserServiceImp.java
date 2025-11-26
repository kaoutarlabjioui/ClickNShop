package org.clicknshop.service.implementation;

import lombok.RequiredArgsConstructor;
import org.clicknshop.dto.request.UserRequestDto;
import org.clicknshop.dto.response.RegisterClientResponseDto;
import org.clicknshop.dto.response.UserRegisterResponseDto;
import org.clicknshop.exception.DuplicateResourceException;
import org.clicknshop.exception.ResourceNotFoundException;
import org.clicknshop.mapper.UserMapper;
import org.clicknshop.model.entity.Client;
import org.clicknshop.model.entity.User;
import org.clicknshop.model.enums.Role;
import org.clicknshop.repository.ClientRepository;
import org.clicknshop.repository.UserRepository;
import org.clicknshop.service.UserService;
import org.clicknshop.util.PasswordGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final UserMapper userMapper;

    private static final int BCRYPT_COST = 10;
    private static final Random RAND = new Random();

    @Override
    @Transactional
    public RegisterClientResponseDto createUserForClient(Client client) {

        if (client.getId() == null) {
            throw new ResourceNotFoundException("Client must be persisted before creating a user");
        }


        String base = slugify(client.getName());
        String username = base + client.getId();


        int attempts = 0;
        while (userRepository.existsByUsername(username) && attempts < 5) {
            username = base + client.getId() + (100 + RAND.nextInt(900));
            attempts++;
        }
        if (userRepository.existsByUsername(username)) {

            username = base + client.getId() + System.currentTimeMillis() % 10000;
        }


        String plainPassword = PasswordGenerator.genAlphanumeric(10);


        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST));


        User user = User.builder()
                .username(username)
                .password(hashed)
                .role(Role.CLIENT)
                .createdAt(LocalDateTime.now())
                .build();


        user = userRepository.save(user);


        client.setUser(user);
        clientRepository.save(client);


        return RegisterClientResponseDto.builder()
                .clientId(client.getId())
                .username(username)
                .temporaryPassword(plainPassword)
                .build();
    }

    @Override
    @Transactional
    public UserRegisterResponseDto createUserAdmin(UserRequestDto userRequestDto){

        if(userRepository.existsByUsername(userRequestDto.getUsername())){
            throw new DuplicateResourceException("this username already exist");
        }

        User user = userMapper.toEntity(userRequestDto);

        String hashedPassword = BCrypt.hashpw(userRequestDto.getPassword(),BCrypt.gensalt(BCRYPT_COST)) ;

        user.setPassword(hashedPassword);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return userMapper.toUserRegisterDto(savedUser);
    }





    private String slugify(String input) {
        if (input == null) return "client";
        String s = input.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
        if (s.isEmpty()) return "client";
        return s;
    }


    public void deleteUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(" User introuvable"));

        clientRepository.findByUserId(id).ifPresent(client -> {
            client.setUser(null);
            clientRepository.save(client);
        });

        userRepository.delete(user);
    }

}