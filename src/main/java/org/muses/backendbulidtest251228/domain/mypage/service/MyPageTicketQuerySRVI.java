package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyTicketResDT;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageTicketQuerySRVI implements MyPageTicketQuerySRV {

    private final MemberRepo memberRepo;
    private final TicketRepo ticketRepo;
    private final RewardRepo rewardRepo;

    @Override
    public List<MyTicketResDT> getMyTickets(UserDetails userDetails) {
        Member me = resolveMember(userDetails);

        List<TicketENT> tickets = ticketRepo.findMyTickets(me.getId());

        return tickets.stream()
                .map(t -> toDto(t))
                .collect(Collectors.toList());
    }

    private MyTicketResDT toDto(TicketENT t) {
        OrderItemENT item = t.getOrderItem();
        ProjectENT project = (item != null) ? item.getProject() : null;

        String optionLabel = null;
        if (item != null && item.getRewardId() != null) {
            RewardENT reward = rewardRepo.findById(item.getRewardId()).orElse(null);
            if (reward != null) {
                // reward_name 우선, 없으면 description
                optionLabel = (reward.getRewardName() != null && !reward.getRewardName().isBlank())
                        ? reward.getRewardName()
                        : reward.getDescription();
            }
        }

        return MyTicketResDT.builder()
                .ticketId(t.getId())
                .projectTitle(project != null ? project.getTitle() : null)
                .opening(project != null ? project.getOpening() : null)
                .optionLabel(optionLabel)
                .ticketToken(t.getTicketToken())
                .status(t.getStatus() != null ? t.getStatus().name() : null)
                .build();
    }

    private Member resolveMember(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }
        return memberRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID, "유효하지 않은 인증 정보입니다."));
    }
}
