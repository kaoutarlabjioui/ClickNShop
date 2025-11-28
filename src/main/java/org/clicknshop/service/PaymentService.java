package org.clicknshop.service;

import org.clicknshop.dto.request.PaymentRequestDto;
import org.clicknshop.dto.response.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto addPayment(Long orderId, PaymentRequestDto dto);
}