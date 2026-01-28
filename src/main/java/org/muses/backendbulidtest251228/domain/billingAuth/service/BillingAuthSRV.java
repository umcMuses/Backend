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
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


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


        OrderENT order = orderREP.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "주문을 찾을 수 없습니다.",
                        Map.of("orderId", orderId)
                ));

        // (1) 상태 검증
        if (order.getStatus() != OrderStatus.RESERVED) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "주문 상태가 올바르지 않습니다.",
                    Map.of(
                            "orderId", orderId,
                            "status", order.getStatus().name(),
                            "expected", OrderStatus.RESERVED.name()
                    )
            );
        }

        // (2) customerKey 검증 (orderId 와 customerKey 가 일치)
        if (order.getCustomerKey() == null || !order.getCustomerKey().equals(req.getCustomerKey())) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "customerKey가 일치하지 않습니다.",
                    Map.of("orderId", orderId)
            );
        }

        // (3) 이미 발급된 빌링 인증 존재 여부
        if (billingAuthREP.findByOrder(order).isPresent()) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "이미 빌링키가 발급된 주문입니다.",
                    Map.of("orderId", orderId)
            );
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
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "이미 빌링 인증 정보가 존재합니다.",
                    Map.of("orderId", orderId)
            );
        }


    }

}
