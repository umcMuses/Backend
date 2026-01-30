package org.muses.backendbulidtest251228.domain.checkin.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinViewDTO;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.orderItem.repository.OrderItemREP;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckinQuerySRV {

    private final OrderItemREP orderItemRep;
    private final RewardRepo rewardRepo;


    // orderItem 기준으로 체크인 화면에 보여줄 정보를 조회한다
    @Transactional(readOnly = true)
    public CheckinViewDTO loadViewByOrderItem(Long orderItemId) {

        OrderItemENT item = orderItemRep.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 아이템"));

        Long projectId = item.getProject().getId();

        RewardENT reward = rewardRepo.findById(item.getRewardId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리워드"));

        Member member = item.getOrder().getMember();


        return new CheckinViewDTO(
                projectId,
                member.getName(),
                member.getNickName(),
                reward.getDescription(),
                item.getQuantity()
        );
    }
}

