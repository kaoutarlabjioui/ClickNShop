    package org.clicknshop.model.entity;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.*;
    import lombok.*;
    import org.clicknshop.model.enums.CustomerTier;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.LocalDateTime;

    @Entity
    @Table(name = "clients")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Client {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "Le nom ne peut pas être vide")
        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
        private String name;

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email doit être valide")
        @Column(nullable = false, unique = true)
        private String email;

        @Enumerated(EnumType.STRING)
        private CustomerTier loyaltyLevel = CustomerTier.BASIC;

        @Min(value = 0, message = "Le total des commandes doit être supérieur ou égal à 0")
        private Integer totalOrders = 0;

        @DecimalMin(value = "0.0", inclusive = true, message = "Le total dépensé doit être positif")
        @Digits(integer = 17, fraction = 2, message = "Le total dépensé doit avoir au maximum 17 chiffres et 2 décimales")
        @Column(precision = 19, scale = 2)
        private BigDecimal totalSpent = BigDecimal.ZERO;

        @PastOrPresent(message = "La date de la première commande ne peut pas être dans le futur")
        private LocalDate firstOrderDate;

        @PastOrPresent(message = "La date de la dernière commande ne peut pas être dans le futur")
        private LocalDate lastOrderDate;

        @PastOrPresent(message = "La date de création ne peut pas être dans le futur")
        private LocalDateTime createdAt;

        @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        @NotNull(message = "Le client doit être associé à un utilisateur")
        private User user;
    }
