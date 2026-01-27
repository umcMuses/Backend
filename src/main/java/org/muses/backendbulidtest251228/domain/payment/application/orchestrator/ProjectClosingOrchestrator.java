package org.muses.backendbulidtest251228.domain.payment.application.orchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.muses.backendbulidtest251228.domain.ticket.service.TicketIssueSRV;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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





        // 2) 성공/실패 판정
        // 실패 흐름
        if (!project.isGoalAchieved()) {
            //  주문의 상태를 RESERVED는 VOID로 무효 처리
            orderTx.voidReservedByProject(projectId);


            // 프로젝트 펀딩 상태를 실패로
            projectTx.finalizeStatusFromClosing(projectId, FundingStatus.FAIL);


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
                            continue;
                        }

                        ticketIssueSRV.issueIfAbsent(item);
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

        // TODO 정산 기능 추가


    }


}
