package org.muses.backendbulidtest251228.domain.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderTxSRV {

    private final OrderREP orderREP;

    // 펀딩 실패로 인한 주문 무효 처리
    @Transactional
    public int voidReservedByProject(Long projectId) {
        return orderREP.voidReservedByProject(projectId);
    }

    //주문 선점
    @Transactional
    public boolean tryAcquirePaying(Long orderId) {
        return orderREP.tryAcquirePaying(orderId) == 1;
    }

    // 성공 반영
    @Transactional
    public boolean markPaidIfPaying(Long orderId) {
        return orderREP.markPaidIfPaying(orderId) == 1;
    }

    //실패 반영
    @Transactional
    public boolean markFailedIfPaying(Long orderId, String reason, LocalDateTime nextRetryAt) {
        return orderREP.markFailedIfPaying(orderId, reason, nextRetryAt) == 1;
    }
}
