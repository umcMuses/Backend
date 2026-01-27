package org.muses.backendbulidtest251228.domain.orderItem.repository;

import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemREP extends JpaRepository<OrderItemENT, Long> {
}
