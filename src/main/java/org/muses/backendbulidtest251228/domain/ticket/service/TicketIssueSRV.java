package org.muses.backendbulidtest251228.domain.ticket.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.checkin.generator.TokenGenerator;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;

import org.muses.backendbulidtest251228.domain.ticket.enums.TicketStatus;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketIssueSRV {

    private final TicketRepo ticketRepo;
    private final TokenGenerator tokenGenerator;

    // 티켓 생성 OrderItem 1개당 티켓 하나 생성
    @Transactional
    public void issueIfAbsent(OrderItemENT orderItem) {

        if (ticketRepo.findByOrderItem(orderItem).isPresent()) {
            return;
        }

        TicketENT ticket = TicketENT.builder()
                .orderItem(orderItem)
                .ticketToken(tokenGenerator.generate())
                .status(TicketStatus.UNUSED)
                .build();

        ticketRepo.save(ticket);
    }
}
