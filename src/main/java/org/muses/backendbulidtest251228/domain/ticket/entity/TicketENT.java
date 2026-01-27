package org.muses.backendbulidtest251228.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.ticket.enums.TicketStatus;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tickets",
        uniqueConstraints = @UniqueConstraint(columnNames = "ticket_token")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TicketENT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    // 주문 항목 1 : 티켓 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemENT orderItem;

    @Column(name = "ticket_token", nullable = false, length = 100)
    private String ticketToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

}
