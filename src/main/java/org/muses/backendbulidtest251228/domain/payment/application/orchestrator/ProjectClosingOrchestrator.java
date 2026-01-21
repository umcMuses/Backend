package org.muses.backendbulidtest251228.domain.payment.application.orchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.payment.application.service.OrderTxSRV;
import org.muses.backendbulidtest251228.domain.payment.application.service.ProjectTxSRV;
import org.muses.backendbulidtest251228.domain.temp.FundingStatus;
import org.muses.backendbulidtest251228.domain.temp.Project;
import org.muses.backendbulidtest251228.domain.temp.ProjectREP;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectClosingOrchestrator {

    private final ProjectREP projectREP;
    private final OrderREP orderREP;

    private final ProjectTxSRV projectTx;
    private final OrderTxSRV orderTx;

    private final PaymentOrchestrator paymentOrchestrator;


    public void processExpiredProjectsOnce(int limit){
        LocalDateTime now = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, limit);

        //마감된 프로젝트 찾기
        List<Project> targets = projectREP.findExpiredActiveProjects(now, pageable);
        log.info("[CLOSE] targets={}", targets.size());


        for (Project p : targets) {
            try {
                processProject(p.getId());
            } catch (Exception e) {
                log.error("[CLOSE] fail | projectId={}", p.getId(), e);
            }
        }


    }


    public void processProject(Long projectId) {



        // 1) 프로젝트 선점 + 조회
        Project project = projectTx.tryAcquireClosingAndGet(projectId);
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
        for (OrderENT o : reserved) {
            try {
                paymentOrchestrator.processOrderPayment(o.getId());
            } catch (Exception e) {
                log.error("[CLOSE] payment fail | projectId={} orderId={}", projectId, o.getId(), e);
            }
        }

        // 4) 프로젝트 SUCCESS 확정(프로젝트 성공과 개별 결제 성공은 분리 정책)
        projectTx.finalizeStatusFromClosing(projectId, FundingStatus.SUCCESS);
        log.info("[CLOSE] project SUCCESS | projectId={}", projectId);


        // TODO 정산 기능 추가
    }


}
