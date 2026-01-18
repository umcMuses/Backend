package org.muses.backendbulidtest251228.domain.order.repository;

import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderREP extends JpaRepository<OrderENT, Long> {
}
