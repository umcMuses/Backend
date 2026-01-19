package org.muses.backendbulidtest251228.domain.payment.application.scheduler;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.payment.application.orchestrator.ProjectClosingOrchestrator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectClosingScheduler {
    private final ProjectClosingOrchestrator orchestrator;

    //1분 마다
    @Scheduled(fixedDelay = 60_000)
    public void run() {
        orchestrator.processExpiredProjectsOnce(100);
    }
}
