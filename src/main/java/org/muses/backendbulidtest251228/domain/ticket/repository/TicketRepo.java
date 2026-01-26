package org.muses.backendbulidtest251228.domain.ticket.repository;

import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketRepo extends JpaRepository<TicketENT, Long> {

    Optional<TicketENT> findByTicketToken(String ticketToken);

    @Modifying
    @Query("""
        update TicketENT t
           set t.status = 'USED',
               t.usedAt = CURRENT_TIMESTAMP
         where t.ticketToken = :token
           and t.status = 'UNUSED'
    """)
    int markUsedIfUnused(@Param("token") String token);
}
