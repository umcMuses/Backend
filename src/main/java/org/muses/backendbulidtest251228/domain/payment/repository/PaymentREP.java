package org.muses.backendbulidtest251228.domain.payment.repository;

import org.muses.backendbulidtest251228.domain.payment.entity.PaymentENT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentREP extends JpaRepository<PaymentENT, Long> {


    Optional<PaymentENT> findByOrderId(Long orderId);

}
