package org.muses.backendbulidtest251228.domain.payment.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.payment.application.orchestrator.ProjectClosingOrchestrator;
import org.muses.backendbulidtest251228.domain.payment.application.service.ProjectTxSRV;
import org.muses.backendbulidtest251228.domain.temp.Project;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClosingCleanupScheduler {

    private final ProjectTxSRV projectTx;
    private final ProjectClosingOrchestrator orchestrator;

    //10분 마다 CLOSING 이 오래 멈춘 프로젝트 재 처리
    @Scheduled(fixedDelay = 600_000)
    public void run() {
        List<Project> stuck = projectTx.findStuckClosing(LocalDateTime.now().minusMinutes(10), 100);
        log.info("[CLEANUP] stuckClosing={}", stuck.size());
        for (Project p : stuck) {
            orchestrator.processProject(p.getId());
        }
    }
}
