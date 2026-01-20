package org.muses.backendbulidtest251228.domain.payment.repository;

import org.muses.backendbulidtest251228.domain.payment.entity.PaymentENT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentREP extends JpaRepository<PaymentENT, Long> {


    Optional<PaymentENT> findByOrder_Id(Long orderId);

    List<PaymentENT> findAllByOrder_Id(Long orderId);

}
