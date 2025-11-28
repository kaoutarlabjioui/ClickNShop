package org.clicknshop.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.clicknshop.model.enums.PaymentStatus;
import org.clicknshop.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Integer paymentNumber;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private LocalDateTime paymentDate;

    private LocalDateTime depositDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String reference;
    private String bank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}