package org.muses.backendbulidtest251228.domain.payment.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.payment.application.orchestrator.PaymentOrchestrator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRetryScheduler {

    private final OrderREP orderREP;
    private final PaymentOrchestrator paymentOrchestrator;

    private static final int MAX_RETRY = 5;

    //5분 마다 PAY_FAILED 라고 적힌거 재시도
    @Scheduled(fixedDelay = 300_000)
    public void run() {
        List<OrderENT> targets = orderREP.findRetryTargets(MAX_RETRY, LocalDateTime.now());
        log.info("[RETRY] targets={}", targets.size());
        for (OrderENT o : targets) {
            paymentOrchestrator.processOrderPayment(o.getId());
        }
    }

}
