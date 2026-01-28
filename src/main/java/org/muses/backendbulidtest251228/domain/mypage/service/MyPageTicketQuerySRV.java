package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyTicketResDT;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageTicketQueryService {

    private final TicketRepo ticketRepo;

    public List<MyTicketResDT> getMyTickets(Long memberId) {
        return ticketRepo.findByMemberId(memberId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private MyTicketResDT toDto(TicketENT ticket) {
        return MyTicketResDT.builder()
                .ticketId(ticket.getTicketId())
                .projectTitle(ticket.getProject().getTitle())
                .eventDateTime(ticket.getProject().getOpeningAt().toString())
                .rewardName(ticket.getReward().getRewardName())
                .status(ticket.getStatus().name())
                .build();
    }
}
