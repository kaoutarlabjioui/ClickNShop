    package org.clicknshop.model.entity;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.*;
    import lombok.*;
    import java.time.LocalDateTime;
    import org.clicknshop.model.enums.Role;

    @Entity
    @Table(name = "users")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(min = 3, max = 100, message = "Le nom d'utilisateur doit contenir entre 3 et 100 caractères")
        private String username;

        @Column(nullable = false)
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        private String password;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @NotNull(message = "Le rôle est obligatoire")
        private Role role;

        @PastOrPresent(message = "La date de création ne peut pas être dans le futur")
        private LocalDateTime createdAt;


        @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
        private Client client;
    }
