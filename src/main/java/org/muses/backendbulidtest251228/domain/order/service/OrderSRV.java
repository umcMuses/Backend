package org.muses.backendbulidtest251228.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.billingAuth.repository.BillingAuthREP;
import org.muses.backendbulidtest251228.domain.order.dto.OrderCreateReqDT;
import org.muses.backendbulidtest251228.domain.order.dto.OrderCreateResDT;
import org.muses.backendbulidtest251228.domain.order.dto.OrderItemReqDT;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.orderItem.repository.OrderItemREP;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.muses.backendbulidtest251228.domain.toss.TossBillingClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSRV {

    private final OrderREP orderREP;
    private final OrderItemREP orderItemREP;
    private final MemberRepo memberRepo;
    private final ProjectRepo projectRepo;
    private final BillingAuthREP billingAuthREP;

    private final TossBillingClient tossBillingClient;


    @Transactional
    public OrderCreateResDT prepare(String baseSuccessUrl, String baseFailUrl, Long userId, OrderCreateReqDT dto) {

        log.info("[Order-Prepare] 주문 생성 시작 - UserId: {}, ProjectId: {}", userId, dto.getProjectId());



        Member member = memberRepo.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "멤버를 찾을 수 없습니다.", Map.of("memberId", userId)));

        ProjectENT project = projectRepo.findById(dto.getProjectId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "프로젝트를 찾을 수 없습니다.", Map.of("projectId", dto.getProjectId())));



        // 일단 UUID 만 사용해 customerKey 가 종속되지 않게 설계
        String customerKey = "muses_" + UUID.randomUUID();

        log.info("[Billing] Prepared new request. customerKey: {}", customerKey);


        //Order 생성

        OrderENT order = OrderENT.builder()
                .member(member)
                .project(project)
                .customerKey(customerKey)
                .status(OrderStatus.RESERVED)
                .totalAmount(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();


        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemReqDT it : dto.getItems()) {

            BigDecimal unitPrice = BigDecimal.valueOf(it.getUnitPrice());
            BigDecimal lineAmount = unitPrice.multiply(BigDecimal.valueOf(it.getQuantity()));

            OrderItemENT item = OrderItemENT.builder()
                    .project(project)
                    .rewardId(it.getRewardId())
                    .quantity(it.getQuantity())
                    .price(unitPrice)
                    .build();

            order.addItem(item);

            totalAmount = totalAmount.add(lineAmount);
        }



            //  주문 총액 반영
        order.changeTotalAmount(totalAmount);

            //  저장
            OrderENT saved = orderREP.save(order);

                return OrderCreateResDT.builder()
                        .orderId(saved.getId())
                        .customerKey(customerKey)
                        .successUrl(baseSuccessUrl)
                        .failUrl(baseFailUrl)
                        .build();


}


// 1) 주문 상세 수량 차감 또는 전체 삭제
// 2) 주문 총액 차감
// 3) 주문 총액이 0원이면:
//    - 빌링키 삭제 요청 (Toss)
//    - BillingAuth 상태 변경
//    - 주문 상태를 CANCELED로 변경

    @Transactional
    public void cancel(Long orderItemId, Integer qty){


        OrderItemENT orderItem = orderItemREP.findById(orderItemId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "주문 상세를 찾을 수 없습니다.",
                        Map.of("orderItemId", orderItemId)
                ));

        OrderENT order = orderItem.getOrder();

        // 주문에서 뺄 금액 = 단가 * 취소수량
        BigDecimal amount = orderItem.getPrice()
                .multiply(BigDecimal.valueOf(qty));

        int remain = orderItem.getQuantity() - qty;

        if (remain > 0) {
            // 수량만 줄이기
            orderItem.changeQuantity(remain);
        } else {
            // 주문상세 전체 삭제
            order.removeItem(orderItem);
        }

        // 주문 총액 차감
        order.changeTotalAmount(order.getTotalAmount().subtract(amount));



        if (order.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            // 주문에 남은 금액이 없으면 빌링키 삭제 + 주문 취소

            BillingAuthENT billingAuth = billingAuthREP.findByOrder(order)
                    .orElseThrow(() -> new BusinessException(
                            ErrorCode.NOT_FOUND,
                            "빌링 인증 정보를 찾을 수 없습니다.",
                            Map.of("orderId", order.getId())
                    ));


            // Toss에 빌링키 삭제 요청
            tossBillingClient.deleteBillingKey(billingAuth.getBillingKey());

            // BillingAuth 상태 변경
            billingAuth.revoke();

            // 주문 상태 변경
            order.changeStatus(OrderStatus.CANCELED);
        }







    }

    //해당 주문을 찾아서 일단 빌링키 삭제 해달라고 toss 에게 요청
    // 이후 빌링키 와 order 상태 변경
    @Transactional
    public void cancel(Long orderId){



        OrderENT order = orderREP.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "주문을 찾을 수 없습니다.",
                        Map.of("orderId", orderId)
                ));



        BillingAuthENT billingAuth = billingAuthREP.findByOrder(order)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "빌링 인증 정보를 찾을 수 없습니다.",
                        Map.of("orderId", order.getId())
                ));
        // Toss에 빌링키 삭제 요청
        tossBillingClient.deleteBillingKey(billingAuth.getBillingKey());


        billingAuth.revoke();

        order.changeStatus(OrderStatus.CANCELED);
    }
}
