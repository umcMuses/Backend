package org.muses.backendbulidtest251228.domain.checkin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinConfirmResDTO;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckinSRV {

    private final TicketRepo ticketRepo;

    // 체크인 링크(token)와 티켓 토큰으로 체크인을 처리한다
    @Transactional
    public CheckinConfirmResDTO confirm( Long ticketId,
                                         String buyerName,
                                         String buyerNickname,
                                         Integer quantity,
                                         String rewardTitle) {


        TicketENT ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓"));



        // 티켓 사용 처리
        int updated = ticketRepo.markUsedIfUnusedById(ticketId);

        if (updated == 0) {
            return CheckinConfirmResDTO.alreadyUsed(ticket.getUsedAt());
        }

        return CheckinConfirmResDTO.usedNow(
                buyerName,
                buyerNickname,
                rewardTitle,
                quantity,
                LocalDateTime.now()
        );
    }
}

