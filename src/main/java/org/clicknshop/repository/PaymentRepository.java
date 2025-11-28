package org.clicknshop.repository;
import org.clicknshop.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    long countByOrderId(Long orderId);

    List<Payment> findByOrderId(Long orderId);
}