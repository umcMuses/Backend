package org.muses.backendbulidtest251228.domain.billingAuth.repository;

import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingAuthREP extends JpaRepository<BillingAuthENT, Long> {

    Optional<BillingAuthENT> findByOrder(OrderENT order);
}
