package org.muses.backendbulidtest251228.domain.checkin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinConfirmResDTO;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinViewDTO;
import org.muses.backendbulidtest251228.domain.checkin.entity.ProjectCheckinLinkENT;
import org.muses.backendbulidtest251228.domain.checkin.repository.ProjectCheckinLinkRepo;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckinSRV {

    private final ProjectCheckinLinkRepo linkRepo;
    private final TicketRepo ticketRepo;
    private final CheckinQuerySRV queryService;

    // 체크인 링크(token)와 티켓 토큰으로 체크인을 처리한다
    @Transactional
    public CheckinConfirmResDTO confirm(String checkinToken, String ticketToken) {

        ProjectCheckinLinkENT link = linkRepo.findByToken(checkinToken)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 체크인 링크"));

        TicketENT ticket = ticketRepo.findByTicketToken(ticketToken)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓"));

        CheckinViewDTO view =
                queryService.loadViewByOrderItem(ticket.getOrderItem().getId());

        // 프로젝트 불일치 차단
        if (!view.getProjectId().equals(link.getProject().getId())) {
            throw new IllegalStateException("다른 프로젝트의 티켓입니다");
        }

        // 티켓 사용 처리
        int updated = ticketRepo.markUsedIfUnused(ticketToken);

        if (updated == 0) {
            return CheckinConfirmResDTO.alreadyUsed(ticket.getUsedAt());
        }

        return CheckinConfirmResDTO.usedNow(
                view.getBuyerName(),
                view.getBuyerNickname(),
                view.getRewardTitle(),
                view.getQuantity(),
                LocalDateTime.now()
        );
    }
}

