package org.muses.backendbulidtest251228.domain.ticket.repository;

import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface TicketRepo extends JpaRepository<TicketENT, Long> {

    Optional<TicketENT> findByTicketToken(String ticketToken);

    Optional<TicketENT> findByOrderItem(OrderItemENT orderItem);

    @Query("""
    select t
    from TicketENT t
    where t.orderItem = :orderItem
""")
    List<TicketENT> findAllByOrderItem(@Param("orderItem") OrderItemENT orderItem);

    @Query("""
    select t
    from TicketENT t
    join fetch t.orderItem
    where t.id = :ticketId
""")
    Optional<TicketENT> findByIdWithOrderItem(@Param("ticketId") Long ticketId);



    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update TicketENT t
       set t.status = 'USED',
           t.usedAt = CURRENT_TIMESTAMP
     where t.id = :ticketId
       and t.status = 'UNUSED'
""")
    int markUsedIfUnusedById(@Param("ticketId") Long ticketId);

    // 내 티켓 리스트
    @Query("""
    select t
      from TicketENT t
      join fetch t.orderItem oi
      join fetch oi.order o
      join fetch oi.project p
     where o.member.id = :memberId
     order by o.createdAt desc
""")
    List<TicketENT> findMyTickets(@Param("memberId") Long memberId);
}
