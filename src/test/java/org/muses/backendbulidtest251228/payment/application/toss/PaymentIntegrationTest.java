package org.muses.backendbulidtest251228.payment.application.toss;


import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.payment.application.orchestrator.PaymentOrchestrator;
import org.muses.backendbulidtest251228.domain.temp.*;
import org.muses.backendbulidtest251228.domain.toss.TossBillingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;



import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Slf4j
class PaymentIntegrationTest {

    @Autowired PaymentOrchestrator paymentOrchestrator;
    @Autowired OrderREP orderREP;
    @Autowired EntityManager em;




    @Test
    @DisplayName("실제 토스 API 연동 테스트 - 성공 사이클")
    void 실제_토스_결제_승인_테스트() {



        log.info(">>> 실제 토스 API 호출 시작");
        paymentOrchestrator.processOrderPayment(34L);
        log.info(">>> 실제 토스 API 호출 완료");




        em.flush();
        em.clear();

        OrderENT reloaded = orderREP.findById(34L).orElseThrow();

        // 실제 통신이므로 결과에 따라 PAID 또는 PAY_FAILED가 결정
        log.info("최종 주문 상태: {}", reloaded.getStatus());


        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.PAID);
    }




}