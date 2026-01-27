package org.muses.backendbulidtest251228.domain.orderItem.repository;

import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemREP extends JpaRepository<OrderItemENT, Long> {
    @Query("""
      SELECT oi.rewardId,
             COALESCE(SUM(oi.quantity), 0),
             COALESCE(SUM(oi.price * oi.quantity), 0)
      FROM OrderItemENT oi
      JOIN oi.order o
      WHERE oi.project.id = :projectId
        AND o.status = 'PAID'
      GROUP BY oi.rewardId
      ORDER BY SUM(oi.quantity) DESC
    """)
    List<Object[]> aggregateRewardSales(@Param("projectId") Long projectId);
}
