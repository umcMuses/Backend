package org.muses.backendbulidtest251228.domain.order.repository;

import jakarta.transaction.Transactional;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderREP extends JpaRepository<OrderENT, Long> {

    List<OrderENT> findByProjectIdAndStatus(Long projectId, OrderStatus status);

    // 프로젝트 실패 처리: RESERVED -> VOID (펀딩 실패 무효)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      UPDATE OrderENT o
      SET o.status='VOID'
      WHERE o.project.id=:projectId AND o.status='RESERVED'
    """)
    int voidReservedByProject(@Param("projectId") Long projectId);


    // 주문 선점: RESERVED / PAY_FAILED -> PAYING
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      UPDATE OrderENT o
      SET o.status='PAYING'
      WHERE o.id=:id
        AND o.status IN ('RESERVED', 'PAY_FAILED')
    """)
    int tryAcquirePaying(@Param("id") Long id);

    //  성공 반영: PAYING -> PAID
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      UPDATE OrderENT o
      SET o.status='PAID'
      WHERE o.id=:id AND o.status='PAYING'
    """)
    int markPaidIfPaying(@Param("id") Long id);

    // 실패 반영: PAYING -> PAY_FAILED (+ retryCount, nextRetryAt)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      UPDATE OrderENT o
      SET o.status='PAY_FAILED',
          o.retryCount = COALESCE(o.retryCount, 0) + 1,
          o.nextRetryAt = :nextRetryAt
      WHERE o.id=:id AND o.status='PAYING'
    """)
    int markFailedIfPaying(@Param("id") Long id,
                           @Param("reason") String reason,
                           @Param("nextRetryAt") LocalDateTime nextRetryAt);

    // 재시도 대상, 재시도 시간 된 애들만 가져옴
    @Query("""
      SELECT o FROM OrderENT o
      WHERE o.status='PAY_FAILED'
        AND COALESCE(o.retryCount,0) < :maxRetry
        AND o.nextRetryAt <= :now
      ORDER BY o.nextRetryAt ASC, o.id ASC
    """)
    List<OrderENT> findRetryTargets(@Param("maxRetry") int maxRetry, @Param("now") LocalDateTime now);

    
    @Query("""
        update OrderENT o
           set o.paymentOrderId = :paymentOrderId
         where o.id = :orderId
    """)
    int updatePaymentOrderId(
            @Param("orderId") Long orderId,
            @Param("paymentOrderId") String paymentOrderId
    );

}
