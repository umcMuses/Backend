package org.muses.backendbulidtest251228.domain.billingAuth.repository;

import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingAuthREP extends JpaRepository<BillingAuthENT, Long> {

    boolean existsByOrder_Id(Long orderId);
}
