package org.clicknshop.config;

import lombok.RequiredArgsConstructor;
import org.clicknshop.model.entity.User;
import org.clicknshop.model.enums.Role;
import org.clicknshop.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class BootstrapAdmin {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository) {
        return args -> {
            String adminUsername = "admin";
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                String raw = "admin123"; // change this after first login in real use
                String hash = BCrypt.hashpw(raw, BCrypt.gensalt(10));
                User admin = User.builder()
                        .username(adminUsername)
                        .password(hash)
                        .role(Role.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build();
                userRepository.save(admin);
                System.out.println("Admin created -> username: " + adminUsername + " password: " + raw);
            }
        };
    }
}