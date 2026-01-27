package org.muses.backendbulidtest251228.domain.payment.application.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.billingAuth.repository.BillingAuthREP;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.payment.application.service.OrderTxSRV;
import org.muses.backendbulidtest251228.domain.payment.application.service.PaymentTxSRV;
import org.muses.backendbulidtest251228.domain.payment.entity.PaymentENT;
import org.muses.backendbulidtest251228.domain.payment.enums.PaymentStatus;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.domain.toss.TossBillingClient;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingApproveResDT;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.stereotype.Service;



import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentOrchestrator {

    private final OrderREP orderREP;
    private final BillingAuthREP billingAuthREP;
    private final RewardRepo rewardRepo;

    private final OrderTxSRV orderTx;
    private final PaymentTxSRV paymentTx;

    private final TossBillingClient tossClient;

    private final ObjectMapper om = new ObjectMapper();
    private static final int MAX_RETRY = 5;


    public boolean processOrderPayment(Long orderId) {
        // 1) 주문 선점
        // row = 0 이면 즉시 종료, 이미 다른 프로세스가 잡음
        if (!orderTx.tryAcquirePaying(orderId)) {
            log.info("[PAY] skip acquire | orderId={}", orderId);
            return false;
        }

        // 2) 주문
        OrderENT order = orderREP.findByIdWithItems(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "주문을 찾을 수 없습니다.",
                        Map.of("orderId", orderId)
                ));


        if (order.getStatus() != OrderStatus.PAYING) {
            log.info("[PAY] status changed | orderId={} status={}", orderId, order.getStatus());
            return false;
        }



        // 3) Payment 준비
        String idemKey = "order:" + orderId; //고정, 식별자
        PaymentENT payment = paymentTx.getOrCreate(orderId, idemKey, order.getTotalAmount());




        String paymentOrderId = order.getPaymentOrderId();

        if (paymentOrderId == null) {
            paymentOrderId = "muses_order_" + orderId + "_" + UUID.randomUUID().toString().substring(0, 8);
            orderTx.updatePaymentOrderId(order.getId(), paymentOrderId);
            order.setPaymentOrderId(paymentOrderId); //  현재 객체에도 직접 세팅
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            orderTx.markPaidIfPaying(orderId);
            return false;
        }

        paymentTx.markRequested(payment.getId());





        BillingApproveResDT res = null;
        Exception ex = null;

        BillingAuthENT billingAuth = null;
        RewardENT reward = null;


        try {
            if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                throw new BusinessException(
                        ErrorCode.BAD_REQUEST,
                        "주문 항목이 없습니다.",
                        Map.of("orderId", orderId)
                );
            }
            Long rewardId = order.getOrderItems().get(0).getRewardId();

            billingAuth = billingAuthREP.findByOrder(order)
                    .orElseThrow(() -> new BusinessException(
                            ErrorCode.BAD_REQUEST,
                            "BillingAuth가 없습니다.",
                            Map.of("orderId", orderId)
                    ));

            reward = rewardRepo.findById(rewardId)
                    .orElseThrow(() -> new BusinessException(
                            ErrorCode.BAD_REQUEST,
                            "존재하지 않는 리워드입니다.",
                            Map.of("rewardId", rewardId)
                    ));

            // 4) pg 호출
            res = tossClient.approveWithBillingKey(
                    billingAuth,
                    payment.getAmount(),
                    reward.getRewardName(),
                    idemKey,
                    paymentOrderId
            );
        } catch (Exception e) {

            ex = new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "PG 호출 중 오류가 발생했습니다.",
                    Map.of("orderId", orderId)
            );
            log.warn("[PAY] PG call error | orderId={} msg={}", orderId, ex.getMessage(), ex);
        }


        // 5) 결과 반영, 성공인 경우 여기서 끝낸다
        if (res != null && "DONE".equals(res.getStatus())) {
            order.setPaymentOrderId(paymentOrderId);
            //결제 성공
            paymentTx.markSuccess(payment.getId(), res.getPaymentKey(), toJson(res));
            //주문 성공
            orderTx.markPaidIfPaying(orderId);
            log.info("[PAY] success | orderId={}", orderId);


            return true;
        }

        String reason = (res != null && res.getFailure() != null && res.getFailure().getMessage() != null)
                ? res.getFailure().getMessage()
                : (ex != null ? ex.getMessage() : "PG 호출 실패");

        paymentTx.markFailed(payment.getId(), reason, toJson(res));

        int currentRetry = order.getRetryCount() == null ? 0 : order.getRetryCount();
        int nextRetryCount = Math.min(currentRetry + 1, MAX_RETRY);
        LocalDateTime nextRetryAt = LocalDateTime.now().plusMinutes(backoffMinutes(nextRetryCount));

        orderTx.markFailedIfPaying(orderId, reason, nextRetryAt);
        log.warn("[PAY] failed | orderId={} nextRetryAt={} reason={}", orderId, nextRetryAt, reason);


        return false;


    }

    private int backoffMinutes ( int retryCount1Based){
        int exp = Math.min(Math.max(retryCount1Based - 1, 0), 4);
        return 1 << exp;
    }

    private String toJson (Object o){
        if (o == null) return null;
        try {
            return om.writeValueAsString(o);
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }

}
