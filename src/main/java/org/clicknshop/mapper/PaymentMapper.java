package org.clicknshop.mapper;

import org.clicknshop.dto.request.PaymentRequestDto;
import org.clicknshop.dto.response.PaymentResponseDto;
import org.clicknshop.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(source = "paymentType", target = "paymentType")
    @Mapping(source = "status", target = "status")
    PaymentResponseDto toDto(Payment payment);

    Payment toEntity(PaymentRequestDto paymentRequestDto);

}