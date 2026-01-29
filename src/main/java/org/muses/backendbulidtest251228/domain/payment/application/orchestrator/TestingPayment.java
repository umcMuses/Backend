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
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TestingPayment {

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


    @PostMapping("/test")
    public ApiResponse<?> test(){
        log.info("[TEST API] Manual project process started for projectId=1000");
        processProject(1000L);
        return ApiResponse.success("OK");
    }
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
        log.info("[PROJECT-CLOSE-START] 프로젝트 마감 프로세스 시작 | ProjectID: {}", projectId);

        // 1) 프로젝트 선점 + 조회
        ProjectENT project = projectTx.tryAcquireClosingAndGet(projectId);
        if (project == null) {
            log.warn("[PROJECT-CLOSE-SKIP] 프로젝트 선점 실패 (이미 처리 중이거나 대상 아님) | ProjectID: {}", projectId);
            return;
        }
        log.info("[PROJECT-CLOSE-ACQUIRE] 프로젝트 선점 성공 | ProjectID: {}, Title: {}", projectId, project.getTitle());

        BigDecimal targetAmount = project.getTargetAmount();

        List<OrderENT> orderListBefore = orderREP.findByProjectIdAndStatus(projectId, OrderStatus.RESERVED);
        BigDecimal totalSumBefore = orderListBefore.stream()
                .map(OrderENT::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("[PROJECT-CLOSE-CHECK] 목표 금액: {}, 현재 예약 합계: {}, 예약 건수: {}건",
                targetAmount, totalSumBefore, orderListBefore.size());

        // 2) 성공/실패 판정
        if (!project.isGoalAchieved()) {
            log.info("[PROJECT-CLOSE-FAIL] 목표 달성 실패 처리 시작 | ProjectID: {}", projectId);

            // 이 프로젝트에 주문한 사람들(중복 제거) 조회
            List<Long> memberIds = orderREP.findDistinctMemberIdsByProjectId(projectId);

            // 주문의 상태를 RESERVED는 VOID로 무효 처리
            orderTx.voidReservedByProject(projectId);
            log.info("[PROJECT-CLOSE-VOID] 관련 주문 VOID 처리 완료");

            // 프로젝트 펀딩 상태를 실패로
            projectTx.finalizeStatusFromClosing(projectId, FundingStatus.FAIL);

            // 여기서 해당 프로젝트에 주문한 사람들에게 각각 알림을 보낸다
            if (!memberIds.isEmpty()) {
                log.info("[PROJECT-CLOSE-ALARM] 실패 알림 발송 대상: {}명", memberIds.size());
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
        log.info("[PROJECT-CLOSE-SUCCESS-FLOW] 목표 달성 성공! 결제 프로세스 진입 | ProjectID: {}", projectId);

        List<OrderENT> reserved = orderREP.findByProjectIdAndStatusFetchItems(projectId, OrderStatus.RESERVED);
        log.info("[PROJECT-PAYMENT-BATCH] 결제 처리 시작 대상: {}건", reserved.size());

        for (OrderENT order : reserved) {
            try {
                log.info("[PROJECT-PAYMENT-TRY] 결제 시도 | OrderID: {}, Amount: {}", order.getId(), order.getTotalAmount());
                boolean success = paymentOrchestrator.processOrderPayment(order.getId());

                if(success){
                    log.info("[PROJECT-PAYMENT-SUCCESS] 결제 성공 | OrderID: {}. 후속 작업(티켓/알림) 진행", order.getId());
                    for (OrderItemENT item : order.getOrderItems()) {
                        Long rewardId = item.getRewardId();

                        RewardENT reward = rewardRepo.findById(rewardId)
                                .orElseThrow(() -> new BusinessException(
                                        ErrorCode.BAD_REQUEST,
                                        "해당 리워드를 찾을 수 없습니다.",
                                        Map.of("rewardId", rewardId, "orderId", order.getId(), "projectId", projectId)
                                ));

                        if (reward.getType() == RewardType.NONE) {
                            log.info("[PROJECT-ALARM-TYPE4] 리워드 없음 - 펀딩 성공 알림 발송 | OrderItemID: {}", item.getId());
                            alarmSRVI.send(
                                    order.getMember().getId(),
                                    4L,
                                    Map.of("projectName", order.getProject().getTitle())
                            );
                            continue;
                        }

                        log.info("[PROJECT-TICKET-ISSUE] 티켓 발행 및 QR 알림 발송 | OrderItemID: {}", item.getId());
                        ticketIssueSRV.issueIfAbsent(item);

                        alarmSRVI.send(
                                order.getMember().getId(),
                                2L,
                                Map.of("projectName", order.getProject().getTitle())
                        );
                    }
                } else {
                    log.warn("[PROJECT-PAYMENT-FAIL] 결제 실패(단일건) | OrderID: {}", order.getId());
                }

            } catch (Exception e) {
                log.error("[CLOSE] payment fail | projectId={} orderId={}", projectId, order.getId(), e);
            }
        }

        // 4) 프로젝트 SUCCESS 확정
        projectTx.finalizeStatusFromClosing(projectId, FundingStatus.SUCCESS);
        log.info("[CLOSE] project SUCCESS | projectId={}", projectId);

        // QR 생성 및 QR 링크 생성
        log.info("[PROJECT-CHECKIN-ISSUE] 프로젝트 체크인/QR 생성 시작");
        checkinIssueSRV.issueIfAbsent(project);

        int count = orderREP.findByProjectIdAndStatus(projectId, OrderStatus.PAY_FAILED).size();

        if(count != 0) {
            LocalDateTime next = LocalDateTime.now().plusHours(24);
            int scheduled = orderTx.scheduleRetryForFailedOrders(projectId, next);
            log.info("[CLOSE] schedule retry for failed orders | projectId={} failCount={} scheduledRetryCount={} nextRetryAt={}",
                    projectId, count, scheduled, next);
        } else {
            log.info("[PROJECT-PAYMENT-COMPLETE] 모든 주문 결제 성공 완료");
        }

        // 결제가 성공한 곳만 합산 로그
        List<OrderENT> orderList = orderREP.findByProjectIdAndStatus(projectId, OrderStatus.PAID);
        BigDecimal totalSum = orderList.stream()
                .map(OrderENT::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("[SETTLEMENT-CALC] 정산 계산 시작 | 최종 PAID 합계: {}, 건수: {}건", totalSum, orderList.size());

        // 1. 수수료 및 지급액 계산
        BigDecimal feeAmount = totalSum.multiply(new BigDecimal("0.1"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal payoutAmount = totalSum.subtract(feeAmount);

        // 2. 기존 데이터 존재 여부 확인 및 처리
        settlementRepo.findByProject(project)
                .ifPresentOrElse(
                        existingSettlement -> {
                            existingSettlement.updateAmountAndStatus(totalSum, feeAmount, payoutAmount, SettlementStatus.IN_PROGRESS);
                            log.info("[SETTLEMENT] 기존 정산 데이터 업데이트 완료 | Total: {}, Fee: {}, Payout: {}", totalSum, feeAmount, payoutAmount);
                        },
                        () -> {
                            SettlementENT newSettlement = SettlementENT.builder()
                                    .project(project)
                                    .totalAmount(totalSum)
                                    .status(SettlementStatus.IN_PROGRESS)
                                    .feeAmount(feeAmount)
                                    .payoutAmount(payoutAmount)
                                    .build();
                            settlementRepo.save(newSettlement);
                            log.info("[SETTLEMENT] 신규 정산 데이터 생성 완료 | Total: {}, Fee: {}, Payout: {}", totalSum, feeAmount, payoutAmount);
                        }
                );

        log.info("[PROJECT-CLOSE-END] 프로젝트 마감 프로세스 최종 종료 | ProjectID: {}", projectId);
    }


}

