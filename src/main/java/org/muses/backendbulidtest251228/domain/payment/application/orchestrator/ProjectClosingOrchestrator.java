package org.muses.backendbulidtest251228.domain.payment.application.orchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.alarm.service.AlarmSRVI;
import org.muses.backendbulidtest251228.domain.checkin.service.ProjectCheckinIssueSRV;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.payment.application.service.OrderTxSRV;
import org.muses.backendbulidtest251228.domain.payment.application.service.ProjectTxSRV;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.enums.RewardType;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.domain.settlement.entity.SettlementENT;
import org.muses.backendbulidtest251228.domain.settlement.enums.SettlementStatus;
import org.muses.backendbulidtest251228.domain.settlement.repository.SettlementRepo;
import org.muses.backendbulidtest251228.domain.ticket.service.TicketIssueSRV;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectClosingOrchestrator {

    private final ProjectRepo projectRepo;
    private final OrderREP orderREP;
    private final RewardRepo rewardRepo;

    private final ProjectTxSRV projectTx;
    private final OrderTxSRV orderTx;

    private final PaymentOrchestrator paymentOrchestrator;

    private final ProjectCheckinIssueSRV checkinIssueSRV;
    private final TicketIssueSRV ticketIssueSRV;
    private final SettlementRepo settlementRepo;

    private final AlarmSRVI alarmSRVI;

    public void processExpiredProjectsOnce(int limit){
        LocalDateTime now = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, limit);

        //마감된 프로젝트 찾기
        List<ProjectENT> targets = projectRepo.findExpiredActiveProjects(now, pageable);
        log.info("[CLOSE] targets={}", targets.size());


        for (ProjectENT p : targets) {
            try {
                processProject(p.getId());
            } catch (Exception e) {
                log.error("[CLOSE] fail | projectId={}", p.getId(), e);
            }
        }


    }


    public void processProject(Long projectId) {



        // 1) 프로젝트 선점 + 조회
        ProjectENT project = projectTx.tryAcquireClosingAndGet(projectId);
        if (project == null) {
            log.info("[CLOSE] skip acquire | projectId={}", projectId);
            return;
        }



        BigDecimal targetAmount = project.getTargetAmount();

        List<OrderENT> orderListBefore = orderREP.findByProjectIdAndStatus(projectId, OrderStatus.RESERVED);
        BigDecimal totalSumBefore = orderListBefore.stream()
                .map(OrderENT::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2) 성공/실패 판정
        // 실패 흐름
        //totalSum 이 더 크면
        if (totalSumBefore.compareTo(targetAmount) < 0) {

            // 목표 금액이 더 크면 즉, 실패하면

            // 이 프로젝트에 주문한 사람들(중복 제거) 조회
            List<Long> memberIds = orderREP.findDistinctMemberIdsByProjectId(projectId);


            //  주문의 상태를 RESERVED는 VOID로 무효 처리
            orderTx.voidReservedByProject(projectId);


            // 프로젝트 펀딩 상태를 실패로
            projectTx.finalizeStatusFromClosing(projectId, FundingStatus.FAIL);

            // 여기서 해당 프로젝트에 주문한 사람들에게 각각 알림을 보낸다
            if (!memberIds.isEmpty()) {
                alarmSRVI.sendToMany(
                        memberIds,
                        5L, // alarm_id = 5 (펀딩 실패 템플릿)
                        Map.of("projectName", project.getTitle())
                );
            }




            log.info("[CLOSE] project FAILED -> orders VOID | projectId={}", projectId);
            return;
        }




        // 3) 성공 흐름: RESERVED 주문 결제

        // 프로젝트 ID 가 동일하고 아직 주문이 되지 않은 주문들 모으기
        List<OrderENT> reserved = orderREP.findByProjectIdAndStatus(projectId, OrderStatus.RESERVED);
        for (OrderENT order : reserved) {
            try {
                boolean  success = paymentOrchestrator.processOrderPayment(order.getId());

                if(success){
                    for (OrderItemENT item : order.getOrderItems()) {
                        Long rewardId = item.getRewardId();

                        RewardENT reward = rewardRepo.findById(rewardId)
                                .orElseThrow(() -> new BusinessException(
                                        ErrorCode.BAD_REQUEST,
                                        "해당 리워드를 찾을 수 없습니다.",
                                        Map.of("rewardId", rewardId, "orderId", order.getId(), "projectId", projectId)
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



            } catch (Exception e) {
                log.error("[CLOSE] payment fail | projectId={} orderId={}", projectId, order.getId(), e);
            }
        }



        // 4) 프로젝트 SUCCESS 확정(프로젝트 성공과 개별 결제 성공은 분리 정책)
        projectTx.finalizeStatusFromClosing(projectId, FundingStatus.SUCCESS);
        log.info("[CLOSE] project SUCCESS | projectId={}", projectId);


        // QR 생성 및 QR 링크 생성

        checkinIssueSRV.issueIfAbsent(project);



        int count = orderREP.findByProjectIdAndStatus(projectId, OrderStatus.PAY_FAILED).size();

        if(count != 0) {
            // 실패한 주문이 있는 경우
            //하루 뒤에 다시 시도

            LocalDateTime next = LocalDateTime.now().plusHours(24); // 하루 뒤


            //예약
            int scheduled = orderTx.scheduleRetryForFailedOrders(projectId, next);
            log.info("[CLOSE] schedule retry for failed orders | projectId={} count={} nextRetryAt={}",
                    projectId, scheduled, next);


        }else{
            // 모든 주문이 성공한 경우

        }




        // 결제가 성공한 곳만
        List<OrderENT> orderList = orderREP.findByProjectIdAndStatus(projectId, OrderStatus.PAID);
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
                            existingSettlement.updateAmountAndStatus(totalSum, feeAmount, payoutAmount, SettlementStatus.IN_PROGRESS);
                            log.info("[SETTLEMENT] Updated existing settlement for projectId={}", project.getId());
                        },
                        () -> {
                            // 없다면 새로 생성 후 저장
                            SettlementENT newSettlement = SettlementENT.builder()
                                    .project(project)
                                    .totalAmount(totalSum)
                                    .status(SettlementStatus.IN_PROGRESS)
                                    .feeAmount(feeAmount)
                                    .payoutAmount(payoutAmount)
                                    .build();
                            settlementRepo.save(newSettlement);
                            log.info("[SETTLEMENT] Created new settlement for projectId={}", project.getId());
                        }
                );







    }


}
