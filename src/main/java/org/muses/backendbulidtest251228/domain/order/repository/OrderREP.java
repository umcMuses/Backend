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
import java.util.Optional;
import java.math.BigDecimal;

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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update OrderENT o
           set o.paymentOrderId = :paymentOrderId
         where o.id = :orderId
    """)
    int updatePaymentOrderId(
            @Param("orderId") Long orderId,
            @Param("paymentOrderId") String paymentOrderId
    );


    @Query("select o from OrderENT o join fetch o.orderItems where o.id = :id")
    Optional<OrderENT> findByIdWithItems(@Param("id") Long id);

    // 내 후원(결제) 내역 목록
    @Query("""
        SELECT o
        FROM OrderENT o
        JOIN FETCH o.project p
        LEFT JOIN FETCH o.payment pay
        WHERE o.member.id = :memberId
        ORDER BY o.createdAt DESC
    """)
    List<OrderENT> findMyOrdersWithProjectAndPayment(@Param("memberId") Long memberId);

    // 내 후원(결제) 내역 상세
    @Query("""
        SELECT DISTINCT o
        FROM OrderENT o
        JOIN FETCH o.project p
        LEFT JOIN FETCH o.payment pay
        LEFT JOIN FETCH o.billingAuth ba
        LEFT JOIN FETCH o.orderItems oi
        WHERE o.member.id = :memberId
          AND o.id = :orderId
    """)
    Optional<OrderENT> findMyOrderDetailForMe(@Param("memberId") Long memberId, @Param("orderId") Long orderId);

    // 프로젝트의 결제된 주문 + 아이템까지 한 번에
    @Query("""
        select distinct o
        from OrderENT o
        left join fetch o.orderItems oi
        where o.project.id = :projectId
          and o.status = 'PAID'
        order by o.createdAt desc
    """)
    List<OrderENT> findPaidOrdersWithItemsByProjectId(@Param("projectId")Long projectId);

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM OrderENT o
        WHERE o.project.id = :projectId
        AND o.status = 'PAID'
    """)
        BigDecimal sumPaidAmountByProject(@Param("projectId") Long projectId);

    @Query("""
        SELECT COUNT(DISTINCT o.member.id)
        FROM OrderENT o
        WHERE o.project.id = :projectId
        AND o.status = 'PAID'
    """)
        long countDistinctPaidMembersByProject(@Param("projectId") Long projectId);

    @Query("""
    select distinct o.member.id
    from OrderENT o
    where o.project.id = :projectId
      and o.status = org.muses.backendbulidtest251228.domain.order.enums.OrderStatus.PAID
""")
    List<Long> findDistinctPaidMemberIdsByProject(@Param("projectId") Long projectId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update OrderENT o
       set o.nextRetryAt = :nextRetryAt,
           o.retryCount = 1
     where o.project.id = :projectId
       and o.status = :status
""")
    int scheduleRetryForFailedOrders(@Param("projectId") Long projectId,
                                     @Param("status") OrderStatus status,
                                     @Param("nextRetryAt") LocalDateTime nextRetryAt);


    @Query("""
    select o
      from OrderENT o
     where o.status = :status
       and o.nextRetryAt is not null
       and o.nextRetryAt <= :now
       and coalesce(o.retryCount, 0) = 1
""")
    List<OrderENT> findRetryTargetsOnce(@Param("status") OrderStatus status,
                                        @Param("now") LocalDateTime now);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update OrderENT o
       set o.nextRetryAt = null,
           o.retryCount = 2
     where o.id = :orderId
       and o.status = :status
""")
    int stopRetry(@Param("orderId") Long orderId,
                  @Param("status") OrderStatus status);


    @Query("select o from OrderENT o join fetch o.orderItems where o.id in :ids")
    List<OrderENT> findAllWithItemsByIds(List<Long> ids);
}
