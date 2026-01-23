package org.muses.backendbulidtest251228.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.billingAuth.repository.BillingAuthREP;
import org.muses.backendbulidtest251228.domain.order.dto.OrderCreateReqDT;
import org.muses.backendbulidtest251228.domain.order.dto.OrderCreateResDT;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.order.exception.OrderErrorCode;
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
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSRV {

    private final OrderREP orderREP;
    private final MemberRepo memberRepo;
    private final ProjectRepo projectRepo;
    private final BillingAuthREP billingAuthREP;

    private final TossBillingClient tossBillingClient;


    @Transactional
    public OrderCreateResDT prepare(String baseSuccessUrl, String baseFailUrl, Long userId, OrderCreateReqDT dto) {

        log.info("[Order-Prepare] 주문 생성 시작 - UserId: {}, ProjectId: {}, RewardId: {}", userId, dto.getProjectId(), dto.getRewardId());


        //예외 처리 방식 추후 변경
        Member member = memberRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));

        ProjectENT project = projectRepo.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("project not found"));


        // 일단 UUID 만 사용해 customerKey 가 종속되지 않게 설계
        String customerKey = "muses_" + UUID.randomUUID();

        log.info("[Billing] Prepared new request. customerKey: {}", customerKey);

        //  Order 생성
        BigDecimal unitPrice = BigDecimal.valueOf(dto.getUnitPrice());
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));

        OrderENT order = OrderENT.builder()
                .member(member)
                .project(project)
                .customerKey(customerKey)
                .status(OrderStatus.RESERVED)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .build();

        // OrderItem 생성
        OrderItemENT item = OrderItemENT.builder()
                .project(project)
                .rewardId(dto.getRewardId())
                .quantity(dto.getQuantity())
                .price(unitPrice)
                .build();


        order.addItem(item);

        OrderENT saved = orderREP.save(order);

        log.info("[Order-Prepare] 주문 생성 완료 - OrderId: {}, CustomerKey: {}, TotalAmount: {}",
                saved.getId(), customerKey, saved.getTotalAmount());


        return OrderCreateResDT.builder()
                .orderId(saved.getId())
                .customerKey(customerKey)
                .successUrl(baseSuccessUrl)
                .failUrl(baseFailUrl)
                .build();
    }

    //해당 주문을 찾아서 일단 빌링키 삭제 해달라고 toss 에게 요청
    // 이후 빌링키 와 order 상태 변경
    @Transactional
    public void cancel(Long orderId){



        OrderENT order = orderREP.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found. orderId=" + orderId));

        OrderENT orderENT = orderREP.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.INVALID));



        BillingAuthENT billingAuth = billingAuthREP.findByOrder(order)
                .orElseThrow(() -> new IllegalStateException("billing auth not found. orderId=" + orderId));

        // Toss에 빌링키 삭제 요청
        tossBillingClient.deleteBillingKey(billingAuth.getBillingKey());


        billingAuth.revoke();

        order.changeStatus(OrderStatus.CANCELED);
    }
}
