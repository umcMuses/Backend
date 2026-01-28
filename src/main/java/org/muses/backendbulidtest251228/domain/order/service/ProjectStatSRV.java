package org.muses.backendbulidtest251228.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.domain.settlement.entity.SettlementENT;
import org.muses.backendbulidtest251228.domain.settlement.enums.SettlementStatus;
import org.muses.backendbulidtest251228.domain.settlement.repository.SettlementRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectStatSRV {

    private final OrderREP orderREP;
    private final RewardRepo rewardRepo;
    private final SettlementRepo settlementRepo;

    /**
     *  프로젝트 통계 / 리워드 / 정산을
     * RESERVED 주문 기준으로 "전체 재계산"
     */
    @Transactional
    public void recalc(ProjectENT project) {

        // 1) RESERVED 주문 조회
        List<OrderENT> reservedOrders =
                orderREP.findByProjectIdAndStatus(project.getId(), OrderStatus.RESERVED);

        // 2) supporterCount
        int supporterCount = reservedOrders.size();

        // 3) 총액 합
        BigDecimal totalSum = reservedOrders.stream()
                .map(OrderENT::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4) achieveRate 계산
        BigDecimal targetAmount = project.getTargetAmount();
        int achieveRate = 0;

        if (targetAmount != null && targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            achieveRate = totalSum
                    .multiply(BigDecimal.valueOf(100))
                    .divide(targetAmount, 0, RoundingMode.FLOOR)
                    .intValue();
        }

        // 5) Project 통계 반영
        project.updateStatistics(achieveRate, supporterCount);

        // 6) Reward soldQuantity 전체 재계산
        Map<Long, Integer> rewardSoldMap = new HashMap<>();
        for (OrderENT o : reservedOrders) {
            for (OrderItemENT oi : o.getOrderItems()) {
                Long rewardId = oi.getRewardId();
                int q = oi.getQuantity() == null ? 0 : oi.getQuantity();
                rewardSoldMap.merge(rewardId, q, Integer::sum);
            }
        }



        // 1) 해당 프로젝트의 모든 리워드 목록 조회 (주문이 없는 리워드까지 0으로 초기화하기 위함)
        List<RewardENT> allRewards = rewardRepo.findByProjectId(project.getId());

// 2) 모든 리워드를 순회하며 soldQuantity 재계산
        for (RewardENT reward : allRewards) {
            // 주문 내역(rewardSoldMap)에 있으면 그 수량을, 없으면 0을 할당
            int updatedSoldQuantity = rewardSoldMap.getOrDefault(reward.getId(), 0);

            // 리워드 엔티티의 수량 업데이트 (Dirty Checking으로 반영)
            reward.changeSoldQuantity(updatedSoldQuantity);
        }

        // 7) Settlement upsert / delete
        if (targetAmount == null) return;

        settlementRepo.findByProject(project).ifPresentOrElse(
                settlement -> {
                    if (totalSum.compareTo(targetAmount) < 0) {
                        settlementRepo.delete(settlement);
                        log.info("[SETTLEMENT] Deleted | projectId={}", project.getId());
                    } else {
                        BigDecimal feeAmount = totalSum.multiply(new BigDecimal("0.1"))
                                .setScale(2, RoundingMode.HALF_UP);
                        BigDecimal payoutAmount = totalSum.subtract(feeAmount);

                        settlement.updateAmount(totalSum, feeAmount, payoutAmount);
                        log.info("[SETTLEMENT] Updated | projectId={}", project.getId());
                    }
                },
                () -> {
                    if (totalSum.compareTo(targetAmount) >= 0) {
                        BigDecimal feeAmount = totalSum.multiply(new BigDecimal("0.1"))
                                .setScale(2, RoundingMode.HALF_UP);
                        BigDecimal payoutAmount = totalSum.subtract(feeAmount);

                        SettlementENT settlement = SettlementENT.builder()
                                .project(project)
                                .totalAmount(totalSum)
                                .status(SettlementStatus.WAITING)
                                .feeAmount(feeAmount)
                                .payoutAmount(payoutAmount)
                                .build();

                        settlementRepo.save(settlement);
                        log.info("[SETTLEMENT] Created | projectId={}", project.getId());
                    }
                }
        );
    }
}
