package org.muses.backendbulidtest251228.domain.payment.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.alarm.service.AlarmSRVI;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.payment.application.orchestrator.PaymentOrchestrator;
import org.muses.backendbulidtest251228.domain.payment.application.service.OrderTxSRV;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.enums.RewardType;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.domain.settlement.entity.SettlementENT;
import org.muses.backendbulidtest251228.domain.settlement.enums.SettlementStatus;
import org.muses.backendbulidtest251228.domain.settlement.repository.SettlementRepo;
import org.muses.backendbulidtest251228.domain.ticket.service.TicketIssueSRV;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRetryScheduler {

    private final OrderREP orderREP;
    private final PaymentOrchestrator paymentOrchestrator;
    private final OrderTxSRV orderTx;

    private final TicketIssueSRV ticketIssueSRV;
    private final RewardRepo rewardRepo;
    private final SettlementRepo settlementRepo;

    private final AlarmSRVI alarmSRVI;



    //5분 마다 PAY_FAILED 라고 적힌거 재시도
    @Scheduled(fixedDelay = 300_000)
    @Transactional
    public void run() {
        List<OrderENT> targets =  orderREP.findRetryTargetsOnce(OrderStatus.PAY_FAILED, LocalDateTime.now());



        log.info("[RETRY] targets={}", targets.size());
        for (OrderENT order : targets) {
            try {

                boolean success = paymentOrchestrator.processOrderPayment(order.getId());

                if (!success) {
                    //  재시도 1번 실패하면 종료: nextRetryAt=null 처리
                    orderTx.stopRetryIfFailed(order.getId());

                    // 여기서 해당 프로젝트에 주문한 사람들에게 각각 알림을 보낸다
                    alarmSRVI.send(
                            order.getMember().getId(),
                            5L, // alarm_id = 5 (펀딩 실패 템플릿)
                            Map.of("projectName", order.getProject().getTitle())
                    );
                }

                // 티켓 생성
                if(success){
                    for (OrderItemENT item : order.getOrderItems()) {
                        Long rewardId = item.getRewardId();

                        RewardENT reward = rewardRepo.findById(rewardId)
                                .orElseThrow(() -> new BusinessException(
                                        ErrorCode.BAD_REQUEST,
                                        "해당 리워드를 찾을 수 없습니다.",
                                        Map.of("rewardId", rewardId, "orderId", order.getId())
                                ));

                        if (reward.getType() == RewardType.NONE) {

                            //  4번 템플릿(펀딩 성공) - 주문자에게 적재
                            alarmSRVI.send(
                                    order.getMember().getId(),
                                    4L,
                                    Map.of("projectName", order.getProject().getTitle())
                            );
                            continue;
                        }

                        ticketIssueSRV.issueIfAbsent(item);

                        // 2번 템플릿(QR 발급) - 주문자에게 적재
                        alarmSRVI.send(
                                order.getMember().getId(),
                                2L,
                                Map.of("projectName", order.getProject().getTitle())
                        );
                    }
                }

                ProjectENT project = order.getProject();


                // 정산 기능 추가


                // 결제가 성공한 주문만
                List<OrderENT> orderList = orderREP.findByProjectIdAndStatus(project.getId(), OrderStatus.PAID);
                BigDecimal totalSum = orderList.stream()
                        .map(OrderENT::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 1. 수수료 및 지급액 계산 (공통)
                BigDecimal feeAmount = totalSum.multiply(new BigDecimal("0.1"))
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal payoutAmount = totalSum.subtract(feeAmount);


                // 2. 기존 데이터 존재 여부 확인 및 처리
                settlementRepo.findByProject(project)
                        .ifPresentOrElse(
                                existingSettlement -> {
                                    // 이미 있다면 값 업데이트 (Dirty Checking)
                                    existingSettlement.updateAmount(totalSum, feeAmount, payoutAmount);
                                    log.info("[SETTLEMENT] Updated existing settlement for projectId={}", project.getId());
                                },
                                () -> {
                                    // 없다면 새로 생성 후 저장
                                    SettlementENT newSettlement = SettlementENT.builder()
                                            .project(project)
                                            .totalAmount(totalSum)
                                            .status(SettlementStatus.WAITING)
                                            .feeAmount(feeAmount)
                                            .payoutAmount(payoutAmount)
                                            .build();
                                    settlementRepo.save(newSettlement);
                                    log.info("[SETTLEMENT] Created new settlement for projectId={}", project.getId());
                                }
                        );






            } catch (Exception e) {
                log.warn("[RETRY] orderId={} failed", order.getId(), e);
            }
        }
    }

}
