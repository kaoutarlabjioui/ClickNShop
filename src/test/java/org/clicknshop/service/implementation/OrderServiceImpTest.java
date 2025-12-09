package org.clicknshop.service.implementation;

import org.clicknshop.dto.request.OrderItemRequestDto;
import org.clicknshop.dto.request.OrderRequestDto;
import org.clicknshop.dto.response.OrderResponseDto;
import org.clicknshop.exception.BusinessException;

import org.clicknshop.mapper.OrderMapper;
import org.clicknshop.model.entity.*;
import org.clicknshop.model.enums.CustomerTier;
import org.clicknshop.model.enums.OrderStatus;
import org.clicknshop.model.enums.PaymentStatus;
import org.clicknshop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromoCodeRepository promoCodeRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private org.clicknshop.service.PromoCodeService promoCodeService;
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private OrderServiceImp orderService;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(orderService, "taxRate", 20.0);
    }

    private Client buildClient() {
        Client c = new Client();
        c.setId(10L);
        c.setName("Client A");
        c.setEmail("a@example.com");
        c.setLoyaltyLevel(CustomerTier.BASIC);
        c.setTotalOrders(0);
        c.setTotalSpent(BigDecimal.ZERO);
        c.setCreatedAt(LocalDateTime.now());
        return c;
    }

    private Product buildProduct(long id, String name, BigDecimal unitPrice, int stock, boolean active) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setUnitPrice(unitPrice);
        p.setAvailableStock(stock);
        p.setActive(active);
        return p;
    }

    private PromoCode buildPromo(long id, String code, double pct, boolean active) {
        PromoCode p = new PromoCode();
        p.setId(id);
        p.setCode(code);
        p.setDiscountPercentage(pct);
        p.setActive(active);
        return p;
    }

    @Test
    void createOrder_success_reservesStock_and_returnsDto() {
        Client client = buildClient();
        Product product = buildProduct(1L, "Prod1", BigDecimal.valueOf(100), 10, true);

        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderRequestDto req = new OrderRequestDto();
        req.setClientId(client.getId());
        req.setItems(List.of(item));
        req.setPromoCode(null);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(123L);
            return o;
        });
        when(orderMapper.toDto(any(Order.class))).thenReturn(OrderResponseDto.builder().id(123L).status("PENDING").build());

        OrderResponseDto resp = orderService.createOrder(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("PENDING");

        assertThat(product.getAvailableStock()).isEqualTo(8);
        verify(productRepository, times(1)).save(product);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).toDto(any(Order.class));
    }


    @Test
    void createOrder_rejected_when_insufficientStock() {
        Client client = buildClient();
        Product product = buildProduct(1L, "Prod1", BigDecimal.valueOf(50), 1, true);

        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderRequestDto req = new OrderRequestDto();
        req.setClientId(client.getId());
        req.setItems(List.of(item));

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(999L);
            return o;
        });
        when(orderMapper.toDto(any(Order.class))).thenReturn(OrderResponseDto.builder().id(999L).status("REJECTED").build());

        OrderResponseDto resp = orderService.createOrder(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("REJECTED");

        assertThat(product.getAvailableStock()).isEqualTo(1);
        verify(productRepository, never()).save(product);
        verify(orderRepository, times(1)).save(any(Order.class));
    }


    @Test
    void createOrder_rejected_when_productInactive() {
        Client client = buildClient();
        Product product = buildProduct(1L, "Prod1", BigDecimal.valueOf(50), 10, false);

        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductId(1L);
        item.setQuantity(1);

        OrderRequestDto req = new OrderRequestDto();
        req.setClientId(client.getId());
        req.setItems(List.of(item));

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(222L);
            return o;
        });
        when(orderMapper.toDto(any(Order.class))).thenReturn(OrderResponseDto.builder().id(222L).status("REJECTED").build());

        OrderResponseDto resp = orderService.createOrder(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("REJECTED");
        verify(productRepository, never()).save(product);
        verify(orderRepository, times(1)).save(any(Order.class));
    }


    @Test
    void createOrder_appliesPromoDiscount_when_present() {
        Client client = buildClient();
        Product product = buildProduct(1L, "Prod1", BigDecimal.valueOf(200), 10, true);
        PromoCode promo = buildPromo(5L, "PROMO-AB12", 5.0, true);

        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderRequestDto req = new OrderRequestDto();
        req.setClientId(client.getId());
        req.setItems(List.of(item));
        req.setPromoCode(promo.getId());

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(promoCodeRepository.findById(promo.getId())).thenReturn(Optional.of(promo));
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(orderCaptor.capture())).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(777L);
            return o;
        });
        when(orderMapper.toDto(any(Order.class))).thenReturn(OrderResponseDto.builder().id(777L).status("PENDING").build());

        OrderResponseDto resp = orderService.createOrder(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("PENDING");

        Order savedOrder = orderCaptor.getValue();

        assertThat(savedOrder.getSubTotalHt()).isEqualByComparingTo(BigDecimal.valueOf(400.00).setScale(2));
        assertThat(savedOrder.getDiscountAmount()).isEqualByComparingTo(BigDecimal.valueOf(20.00).setScale(2));
        verify(productRepository, times(1)).save(product);
    }


    @Test
    void confirmOrder_updatesClientAndOrder_when_paid() {
        Client client = buildClient();
        Order order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setStatus(OrderStatus.PENDING);
        order.setRemainingAmount(BigDecimal.ZERO);
        order.setTotalTtc(BigDecimal.valueOf(1200.00));
        order.setCreatedAt(LocalDateTime.now());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toDto(any(Order.class))).thenReturn(OrderResponseDto.builder().id(1L).status("CONFIRMED").build());

        OrderResponseDto resp = orderService.confirmOrder(1L);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("CONFIRMED");

        assertThat(client.getTotalOrders()).isEqualTo(1);
        assertThat(client.getTotalSpent()).isEqualByComparingTo(BigDecimal.valueOf(1200.00));
        assertThat(client.getLoyaltyLevel()).isNotNull();
    }

    @Test
    void confirmOrder_throws_when_notPending() {
        Order order = new Order();
        order.setId(2L);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.confirmOrder(2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Seules les commandes PENDING");
    }

    @Test
    void confirmOrder_throws_when_notFullyPaid() {
        Order order = new Order();
        order.setId(3L);
        order.setStatus(OrderStatus.PENDING);
        order.setRemainingAmount(BigDecimal.valueOf(10));

        when(orderRepository.findById(3L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.confirmOrder(3L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("n'est pas totalement payée");
    }


    @Test
    void cancelOrder_restoresStock_and_rejectsPayments_and_setsCanceled() {
        Product p1 = buildProduct(1L, "p1", BigDecimal.valueOf(100), 5, true);
        OrderItem it = new OrderItem();
        it.setId(11L);
        it.setProduct(p1);
        it.setQuantity(2);

        Order order = new Order();
        order.setId(10L);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(List.of(it));
        order.setTotalTtc(BigDecimal.valueOf(500));
        order.setRemainingAmount(BigDecimal.valueOf(100));

        Payment pay = new Payment();
        pay.setId(2L);
        pay.setAmount(BigDecimal.valueOf(100));
        pay.setStatus(PaymentStatus.ENCAISSE);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(paymentRepository.findByOrderId(order.getId())).thenReturn(List.of(pay));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toDto(any(Order.class))).thenReturn(OrderResponseDto.builder().id(order.getId()).status("CANCELED").build());

        OrderResponseDto resp = orderService.cancelOrder(order.getId());

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("CANCELED");

        assertThat(p1.getAvailableStock()).isEqualTo(7);

        assertThat(pay.getStatus()).isEqualTo(PaymentStatus.REJETE);
        assertThat(pay.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void cancelOrder_throws_when_notPending() {
        Order order = new Order();
        order.setId(20L);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(20L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Seules les commandes PENDING");
    }
}