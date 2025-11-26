package org.clicknshop.repository;

import org.clicknshop.model.entity.PromoCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCodeAndDeletedFalse(String code);
    boolean existsByCode(String code);
    Page<PromoCode> findByDeletedFalse(Pageable pageable);
}
