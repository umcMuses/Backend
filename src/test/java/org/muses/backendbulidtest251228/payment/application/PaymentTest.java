package org.muses.backendbulidtest251228.payment.application;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.billingAuth.enums.BillingAuthStatus;
import org.muses.backendbulidtest251228.domain.billingAuth.enums.PgProvider;
import org.muses.backendbulidtest251228.domain.billingAuth.repository.BillingAuthREP;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.enums.Provider;
import org.muses.backendbulidtest251228.domain.member.enums.Role;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.payment.application.orchestrator.PaymentOrchestrator;
import org.muses.backendbulidtest251228.domain.payment.entity.PaymentENT;
import org.muses.backendbulidtest251228.domain.payment.repository.PaymentREP;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.enums.AgeLimit;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.enums.Region;
import org.muses.backendbulidtest251228.domain.project.enums.RewardType;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.domain.toss.TossBillingClient;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingApproveResDT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@SpringBootTest
@Transactional
@Slf4j
class PaymentTest {

    @Autowired PaymentOrchestrator paymentOrchestrator;

    @Autowired OrderREP orderREP;
    @Autowired PaymentREP paymentREP;
    @Autowired BillingAuthREP billingAuthREP;
    @Autowired
    ProjectRepo projectREP;
    @Autowired
    MemberRepo memberREP;
    @Autowired EntityManager em;


    @Autowired
    RewardRepo rewardRepository;


    @MockitoBean
    TossBillingClient tossBillingClient;

    @Test
    @DisplayName("단일 결제 1회 호출 -> PG approve 1회 + Order=PAID + Payment 1개 생성")
    void 결제_한사이클_성공() {
        // given
        OrderENT order = createTestOrder(OrderStatus.RESERVED);
        createTestBillingAuth(order);


        BillingApproveResDT ok = new BillingApproveResDT();
        ok.setStatus("DONE");
        ok.setPaymentKey("pay_" + UUID.randomUUID());
        ok.setOrderId("po_" + UUID.randomUUID());
        ok.setTotalAmount(10000L);
        ok.setApprovedAt(LocalDateTime.now().toString());
        ok.setFailure(new BillingApproveResDT.Failure());

        given(tossBillingClient.approveWithBillingKey(
                any(), // BillingAuthENT
                any(), // BigDecimal
                any(), // String (title)
                any(), // String (idemKey)
                any()  // String (paymentOrderId)
        )).willReturn(ok);

        em.flush();
        em.clear();



        //  같은 트랜잭션 안에서 order 다시 조회 + orderItems 초기화
        OrderENT attached = orderREP.findById(order.getId()).orElseThrow();
        attached.getOrderItems().size();


        // when
        OrderENT before = orderREP.findById(order.getId()).get();
        log.info("진입 전 DB 상태: {}", before.getStatus());

        paymentOrchestrator.processOrderPayment(attached.getId());

        // then
        em.flush();
        em.clear();

        List<PaymentENT> payments = paymentREP.findAllByOrder_Id(order.getId());
        assertThat(payments).hasSize(1);

        OrderENT reloaded = orderREP.findById(order.getId()).orElseThrow();




        if (reloaded.getStatus() == OrderStatus.PAY_FAILED) {
            log.error("테스트 실패! 실제 상태가 PAY_FAILED임.");

        }


        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.PAID);

        then(tossBillingClient).should(times(1)).approveWithBillingKey(
                any(BillingAuthENT.class),
                any(BigDecimal.class),
                anyString(),
                anyString(),
                anyString()
        );
    }


    // ===== 테스트 데이터 생성 =====

    private OrderENT createTestOrder(OrderStatus status) {
        LocalDateTime now = LocalDateTime.now();



        Member member = memberREP.save(
                Member.builder()
                        .provider(Provider.LOCAL)
                        .role(Role.MAKER)
                        .name("테스트유저")
                        .email("test_" + UUID.randomUUID() + "@muses.com")
                        .providerId("local_" + UUID.randomUUID())
                        .passwd("test-password")
                        .phoneNumber("01012345678")
                        .nickName("tester_" + UUID.randomUUID())
                        .build()
        );


        // member 변경
        ProjectENT project = projectREP.save(
                ProjectENT.builder()
                        .member(member)
                        .status("DRAFT")
                        .lastSavedStep(1)
                        .title("뮤지컬 <봄의 노래>")
                        .description("청춘을 노래하는 소규모 뮤지컬 공연")
                        .ageLimit(AgeLimit.ALL)
                        .region(Region.SEOUL)
                        .targetAmount(new BigDecimal("10000000"))
                        .deadline(LocalDateTime.of(2026, 3, 31, 23, 59))
                        .opening(LocalDateTime.of(2026, 4, 10, 19, 0))
                        .achieveRate(0)
                        .supporterCount(0)
                        .fundingStatus(FundingStatus.FUNDING)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        RewardENT reward = rewardRepository.save(
                RewardENT.builder()
                        .project(project)
                        .rewardName("얼리버드 티켓")
                        .description("공연 20% 할인권 포함")
                        .price(new BigDecimal("10000"))
                        .type(RewardType.NONE)
                        .build()
        );





        OrderENT order = OrderENT.builder()
                .project(project)
                .member(member)
                .totalAmount(new BigDecimal("10000"))
                .status(status)
                .createdAt(now)
                .customerKey("ck_" + UUID.randomUUID())
                .build();


        OrderItemENT item = OrderItemENT.builder()
                .project(project)
                .rewardId(reward.getId())
                .quantity(1)
                .price(new BigDecimal("10000"))
                .build();

        order.addItem(item);
        return orderREP.save(order);
    }

    private void createTestBillingAuth(OrderENT order) {
        billingAuthREP.save(
                BillingAuthENT.builder()
                        .order(order)
                        .customerKey(order.getCustomerKey())
                        .provider(PgProvider.TOSS)
                        .billingKey("bill_" + UUID.randomUUID())
                        .cardCompany("SAMSUNG")
                        .cardNumberMasked("1234-****-****-5678")
                        .status(BillingAuthStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }


}
