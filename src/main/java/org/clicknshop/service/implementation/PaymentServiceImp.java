package org.clicknshop.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clicknshop.dto.request.PaymentRequestDto;
import org.clicknshop.dto.response.PaymentResponseDto;
import org.clicknshop.exception.BusinessException;
import org.clicknshop.exception.ResourceNotFoundException;
import org.clicknshop.mapper.PaymentMapper;
import org.clicknshop.model.entity.Order;
import org.clicknshop.model.entity.Payment;
import org.clicknshop.model.enums.PaymentStatus;
import org.clicknshop.model.enums.PaymentType;
import org.clicknshop.repository.OrderRepository;
import org.clicknshop.repository.PaymentRepository;
import org.clicknshop.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImp implements PaymentService {

    private static final BigDecimal PAYMENT_LIMIT = BigDecimal.valueOf(20000);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponseDto addPayment(Long orderId, PaymentRequestDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));

        BigDecimal amount = dto.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Le montant du paiement doit être > 0");
        }

        if ( amount.compareTo(PAYMENT_LIMIT) > 0 ) {
            throw new BusinessException("Un paiement ne peut pas dépasser " + PAYMENT_LIMIT);
        }

        long count = paymentRepository.countByOrderId(orderId);
        int paymentNumber = (int) count + 1;

        Payment payment = Payment.builder()
                .paymentNumber(paymentNumber)
                .amount(amount)
                .paymentType(PaymentType.valueOf(dto.getPaymentType()))
                .paymentDate(LocalDateTime.now())
                .depositDate(dto.getDepositDate())
                .reference(dto.getReference())
                .bank(dto.getBank())
                .order(order)
                .build();

        if (payment.getPaymentType() == PaymentType.CASH) {
            payment.setStatus(PaymentStatus.ENCAISSE);
        } else {
            if (payment.getDepositDate() == null || !payment.getDepositDate().isAfter(LocalDateTime.now())) {
                payment.setStatus(PaymentStatus.ENCAISSE);
            } else {
                payment.setStatus(PaymentStatus.EN_ATTENTE);
            }
        }

        paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.ENCAISSE) {
            BigDecimal remaining = order.getRemainingAmount().subtract(payment.getAmount()).max(BigDecimal.ZERO);
            order.setRemainingAmount(remaining);
            orderRepository.save(order);
        }

        return paymentMapper.toDto(payment);
    }
}