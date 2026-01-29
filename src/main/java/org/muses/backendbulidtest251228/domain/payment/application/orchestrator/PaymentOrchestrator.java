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
        log.info("[PAY-START] 결제 프로세스 진입 | orderId: {}", orderId);

        // 1) 주문 선점
        if (!orderTx.tryAcquirePaying(orderId)) {
            log.warn("[PAY-SKIP] 주문 선점 실패 (이미 처리 중이거나 상태 부적절) | orderId: {}", orderId);
            return false;
        }

        try {
            // 2) 주문 조회 및 검증
            OrderENT order = orderREP.findByIdWithItems(orderId)
                    .orElseThrow(() -> new BusinessException(
                            ErrorCode.NOT_FOUND,
                            "주문을 찾을 수 없습니다.",
                            Map.of("orderId", orderId)
                    ));

            if (order.getStatus() != OrderStatus.PAYING) {
                log.warn("[PAY-ABORT] 주문 상태가 PAYING이 아님 | orderId: {}, currentStatus: {}", orderId, order.getStatus());
                return false;
            }

            // 3) Payment 준비
            String idemKey = "order:" + orderId;
            PaymentENT payment = paymentTx.getOrCreate(orderId, idemKey, order.getTotalAmount());
            log.info("[PAY-PREPARE] Payment 레코드 생성/확인 | paymentId: {}, amount: {}", payment.getId(), payment.getAmount());

            String paymentOrderId = order.getPaymentOrderId();
            if (paymentOrderId == null) {
                paymentOrderId = "muses_order_" + orderId + "_" + UUID.randomUUID().toString().substring(0, 8);
                orderTx.updatePaymentOrderId(order.getId(), paymentOrderId);
                order.setPaymentOrderId(paymentOrderId);
                log.info("[PAY-ID-GEN] 신규 결제 주문번호 생성: {}", paymentOrderId);
            }

            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                log.info("[PAY-ALREADY-DONE] 이미 성공한 결제건입니다 | orderId: {}", orderId);
                orderTx.markPaidIfPaying(orderId);
                return true;
            }

            paymentTx.markRequested(payment.getId());
            log.info("[PAY-REQUEST-PG] PG 승인 요청 준비 중... | paymentId: {}", payment.getId());

            BillingApproveResDT res = null;
            Exception ex = null;

            try {
                // 비즈니스 데이터 검증
                if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "주문 항목 누락", Map.of("orderId", orderId));
                }

                Long rewardId = order.getOrderItems().get(0).getRewardId();
                BillingAuthENT billingAuth = billingAuthREP.findByOrder(order)
                        .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "BillingAuth 누락"));

                RewardENT reward = rewardRepo.findById(rewardId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "리워드 정보 없음"));

                // 4) PG 호출
                log.info("[PG-CALL] Toss 승인 API 호출 시작 | idemKey: {}", idemKey);
                res = tossClient.approveWithBillingKey(billingAuth, payment.getAmount(), reward.getRewardName(), idemKey, paymentOrderId);
                log.info("[PG-RESPONSE] Toss 응답 수신 완료 | status: {}", res.getStatus());

            } catch (Exception e) {
                ex = e;
                log.error("[PG-ERROR] PG 호출 중 예외 발생 | orderId: {}, msg: {}", orderId, e.getMessage());
            }

            // 5) 결과 반영
            if (res != null && "DONE".equals(res.getStatus())) {
                paymentTx.markSuccess(payment.getId(), res.getPaymentKey(), toJson(res));
                orderTx.markPaidIfPaying(orderId);
                log.info("[PAY-SUCCESS] 결제 최종 성공 처리 완료 | orderId: {}, paymentKey: {}", orderId, res.getPaymentKey());
                return true;
            }

            // 실패 처리 로직
            String reason = (res != null && res.getFailure() != null) ? res.getFailure().getMessage() : (ex != null ? ex.getMessage() : "PG 응답 없음");
            paymentTx.markFailed(payment.getId(), reason, toJson(res));

            int currentRetry = (order.getRetryCount() == null) ? 0 : order.getRetryCount();
            int nextRetryCount = Math.min(currentRetry + 1, MAX_RETRY);
            LocalDateTime nextRetryAt = LocalDateTime.now().plusMinutes(backoffMinutes(nextRetryCount));

            orderTx.markFailedIfPaying(orderId, reason, nextRetryAt);
            log.warn("[PAY-FAILED] 결제 실패 처리 (재시도 예정) | orderId: {}, 사유: {}, 다음시도: {}, 시도횟수: {}/{}",
                    orderId, reason, nextRetryAt, nextRetryCount, MAX_RETRY);

        } catch (Exception e) {
            log.error("[PAY-CRITICAL] 오케스트레이터 예상치 못한 오류 | orderId: {}", orderId, e);
        }

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
