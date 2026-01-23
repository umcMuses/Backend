package org.muses.backendbulidtest251228.domain.billingAuth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.billingAuth.dto.BillingAuthIssueReqDTO;
import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.billingAuth.repository.BillingAuthREP;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.toss.TossBillingClient;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingIssueResDT;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class BillingAuthSRV{

    private final TossBillingClient tossBillingClient;
    private final OrderREP orderREP;
    private final BillingAuthREP billingAuthREP;


    //  authKey + customerKey 로 billingKey 발급하고 billing_auth 저장
    @Transactional
    public void issueBillingKey(BillingAuthIssueReqDTO req, Long orderId) {


        // 예외 처리 임시
        OrderENT order = orderREP.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found. orderId=" + orderId));

        // (1) 상태 검증
        if (order.getStatus() != OrderStatus.RESERVED) {
            throw new IllegalStateException("order status invalid. status=" + order.getStatus());
        }

        // (2) customerKey 검증, orderId 와 customerKey 가 일치
        if (!order.getCustomerKey().equals(req.getCustomerKey())) {
            throw new IllegalArgumentException("customerKey mismatch");
        }

        if (billingAuthREP.findByOrder(order).isPresent()) {
            throw new IllegalStateException("billing auth already exists. orderId=" + orderId);
        }




        BillingIssueResDT issued =
                tossBillingClient.issueBillingKey(req.getAuthKey(), req.getCustomerKey());

        BillingAuthENT billingAuth = BillingAuthENT.active(
                order,
                issued.getCustomerKey(),
                issued.getBillingKey(),
                issued.getCardCompany(),
                issued.getCardNumber()
        );

        try {
            billingAuthREP.save(billingAuth);
            billingAuthREP.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("billing auth already exists. orderId=" + orderId, e);
        }


    }

}
