package org.muses.backendbulidtest251228.domain.ticket.repository;

import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketRepo extends JpaRepository<TicketENT, Long> {

    Optional<TicketENT> findByTicketToken(String ticketToken);

    Optional<TicketENT> findByOrderItem(OrderItemENT orderItem);


    @Modifying (clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update TicketENT t
           set t.status = 'USED',
               t.usedAt = CURRENT_TIMESTAMP
         where t.ticketToken = :token
           and t.status = 'UNUSED'
    """)
    int markUsedIfUnused(@Param("token") String token);
}
