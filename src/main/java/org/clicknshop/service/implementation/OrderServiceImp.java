package org.clicknshop.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clicknshop.dto.request.OrderItemRequestDto;
import org.clicknshop.dto.request.OrderRequestDto;
import org.clicknshop.dto.response.OrderResponseDto;
import org.clicknshop.exception.BusinessException;
import org.clicknshop.exception.ResourceNotFoundException;
import org.clicknshop.mapper.OrderMapper;
import org.clicknshop.model.entity.*;
import org.clicknshop.model.enums.CustomerTier;
import org.clicknshop.model.enums.OrderStatus;
import org.clicknshop.model.enums.PaymentStatus;
import org.clicknshop.repository.*;
import org.clicknshop.service.OrderService;
import org.clicknshop.service.PromoCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImp implements OrderService {

    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final PromoCodeService promoCodeService;
    private final PaymentRepository paymentRepository;
    @Value("${tax.rate:20}")
    private double taxRate;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto){

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        if(dto.getItems() == null || dto.getItems().isEmpty()){
            throw new BusinessException("La commande doit contenir au moins un article");
        }

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal subTotal= BigDecimal.ZERO;

        for(OrderItemRequestDto itemReq : dto.getItems()){
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(()->new ResourceNotFoundException("Produit non disponible id="+itemReq.getProductId()));
            if(!product.isActive()){
                throw new BusinessException("Produit non disponible");
            }

            if(product.getAvailableStock()<itemReq.getQuantity()){
                order.setStatus(OrderStatus.REJECTED);
                orderRepository.save(order);

                throw new BusinessException("Stock insuffisant pour le produit :"+product.getName());
            }

            Integer newStock = product.getAvailableStock()-itemReq.getQuantity();
            product.setAvailableStock(newStock);
            productRepository.save(product);

            BigDecimal unitPrice = product.getUnitPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            subTotal = subTotal.add(lineTotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .unitPrice(unitPrice)
                    .quantity(itemReq.getQuantity())
                    .lineTotal(lineTotal)
                    .order(order)
                    .build();

            order.getItems().add(orderItem);


        }

    double loyaltyPct = computeLoyaltyPercentage(client.getLoyaltyLevel(),subTotal);
     BigDecimal loyaltyDiscount = subTotal.multiply(BigDecimal.valueOf(loyaltyPct/100.0));

     BigDecimal promoDiscount = BigDecimal.ZERO;
     PromoCode appliedPromo = null;

     if(dto.getPromoCode() !=null && !dto.getPromoCode().isBlank()){
         appliedPromo = promoCodeRepository.findByCodeAndDeletedFalse(dto.getPromoCode().toUpperCase())
                 .orElseThrow(() -> new BusinessException("Code promo invalide"));

         if (!appliedPromo.isActive()) {
             throw new BusinessException("Code promo inactif");
         }

     }

     BigDecimal baseForPromo = subTotal.subtract(loyaltyDiscount);
     promoDiscount = baseForPromo.multiply(BigDecimal.valueOf(appliedPromo.getDiscountPercentage()/100.0));

        BigDecimal totalDiscount = loyaltyDiscount.add(promoDiscount);
        BigDecimal htAfter = subTotal.subtract(totalDiscount).max(BigDecimal.ZERO);
        BigDecimal tax = htAfter.multiply(BigDecimal.valueOf(taxRate / 100.0));
        BigDecimal totalTtc = htAfter.add(tax);

        order.setSubTotalHt(subTotal);
        order.setDiscountAmount(totalDiscount);
        order.setHtAfterDiscount(htAfter);
        order.setTaxAmount(tax);
        order.setTotalTtc(totalTtc);
        order.setRemainingAmount(totalTtc);
        order.setStatus(OrderStatus.PENDING);
        order.setClient(client);
        order.setPromoCode(appliedPromo);
        Order saved = orderRepository.save(order);

        return orderMapper.toDto(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersByClient(Long clientId, Pageable pageable) {
        Page<Order> page = orderRepository.findByClientId(clientId, pageable);
        return page.map(orderMapper::toDto);
    }


    @Override
    @Transactional
    public OrderResponseDto confirmOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Seules les commandes PENDING peuvent être confirmées");
        }

        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("La commande n'est pas totalement payée");
        }

        Client client = order.getClient();
        int newTotalOrders = (client.getTotalOrders() == null ? 0 : client.getTotalOrders()) + 1;
        client.setTotalOrders(newTotalOrders);
        BigDecimal prevTotal = client.getTotalSpent() == null ? BigDecimal.ZERO : client.getTotalSpent();
        client.setTotalSpent(prevTotal.add(order.getTotalTtc()));

        if (client.getFirstOrderDate() == null) {
            client.setFirstOrderDate(order.getCreatedAt().toLocalDate());
        }
        client.setLastOrderDate(order.getCreatedAt().toLocalDate());

        client.setLoyaltyLevel(computeTier(newTotalOrders, client.getTotalSpent()));
        clientRepository.save(client);

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }


    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Seules les commandes PENDING peuvent être annulées");
        }

        for (OrderItem item : order.getItems()) {
            Product product = null;
            if (item.getProduct() != null && item.getProduct().getId() != null) {
                product = productRepository.findById(item.getProduct().getId()).orElse(null);
            }
            if (product != null) {
                product.setAvailableStock(product.getAvailableStock() + item.getQuantity());
                productRepository.save(product);
            }
        }


        List<Payment> payments = paymentRepository.findByOrderId(order.getId());
        if (payments != null && !payments.isEmpty()) {
            for (Payment p : payments) {

                p.setStatus(PaymentStatus.REJETE);

                p.setAmount(BigDecimal.ZERO);
                paymentRepository.save(p);
            }
        }


        order.setRemainingAmount(order.getTotalTtc());

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }




    private double computeLoyaltyPercentage(CustomerTier tier, BigDecimal subTotal) {
        if (tier == null) return 0.0;
        switch (tier) {
            case SILVER:
                return subTotal.compareTo(BigDecimal.valueOf(500)) >= 0 ? 5.0 : 0.0;
            case GOLD:
                return subTotal.compareTo(BigDecimal.valueOf(800)) >= 0 ? 10.0 : 0.0;
            case PLATINUM:
                return subTotal.compareTo(BigDecimal.valueOf(1200)) >= 0 ? 15.0 : 0.0;
            default:
                return 0.0;
        }
    }

    private CustomerTier computeTier(int totalOrders, BigDecimal totalSpent) {
        if (totalOrders >= 20 || totalSpent.compareTo(BigDecimal.valueOf(15000)) >= 0) return CustomerTier.PLATINUM;
        if (totalOrders >= 10 || totalSpent.compareTo(BigDecimal.valueOf(5000)) >= 0) return CustomerTier.GOLD;
        if (totalOrders >= 3 || totalSpent.compareTo(BigDecimal.valueOf(1000)) >= 0) return CustomerTier.SILVER;
        return CustomerTier.BASIC;
    }


}
