package org.muses.backendbulidtest251228.domain.payment.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.payment.entity.PaymentENT;
import org.muses.backendbulidtest251228.domain.payment.enums.PaymentStatus;
import org.muses.backendbulidtest251228.domain.payment.repository.PaymentREP;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTxSRV {

    private final PaymentREP paymentREP;
    private final OrderREP orderREP;


    // 동시성 충돌 복구
    @Transactional
    public PaymentENT getOrCreate(Long orderId, String idemKey, BigDecimal amount) {
        return paymentREP.findByOrder_Id(orderId).orElseGet(() -> {
            try {
                OrderENT order = orderREP.findById(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("order not found. orderId=" + orderId));


                return paymentREP.save(PaymentENT.builder()
                        .order(order)
                        .idemKey(idemKey)
                        .amount(amount)
                        .status(PaymentStatus.READY)
                        .build());
            } catch (DataIntegrityViolationException e) {
                // 동시 생성 충돌 -> 재조회로 복구
                return paymentREP.findByOrder_Id(orderId).orElseThrow(() -> e);
            }
        });
    }

    // 결제 진행중으로 변경
    @Transactional
    public void markRequested(Long paymentId) {
        // 임시 예외 처리
        PaymentENT p = paymentREP.findById(paymentId).orElseThrow();
        if (p.getStatus() == PaymentStatus.SUCCESS) return;

        paymentREP.save(p);
    }

    // 결제 성공으로 저장
    @Transactional
    public void markSuccess(Long paymentId, String paymentKey, String resBody) {

        PaymentENT p = paymentREP.findById(paymentId).orElseThrow();
        if (p.getStatus() == PaymentStatus.SUCCESS) return;
        p.markSuccess(paymentKey, resBody);
        paymentREP.save(p);
    }

    // 결제 실패 저장
    @Transactional
    public void markFailed(Long paymentId, String reason, String resBody) {
        PaymentENT p = paymentREP.findById(paymentId).orElseThrow();
        if (p.getStatus() == PaymentStatus.SUCCESS) {
            log.warn("이미 성공한 결제는 실패 처리 불가. paymentId={}", paymentId);
            return;
        }


        p.markFailed(reason, resBody);
        paymentREP.save(p);
    }

}
