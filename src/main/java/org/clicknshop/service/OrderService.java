package org.clicknshop.service;

;

import org.clicknshop.dto.request.OrderRequestDto;
import org.clicknshop.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto dto);

    OrderResponseDto getById(Long id);

    Page<OrderResponseDto> getOrdersByClient(Long clientId, Pageable pageable);
    Page<OrderResponseDto> getOrdersForCurrentUser(Pageable pageable);

    OrderResponseDto confirmOrder(Long id);

    OrderResponseDto cancelOrder(Long id);
}