package org.muses.backendbulidtest251228.domain.toss;

import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingApproveReqDTO;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingApproveResDTO;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingIssueReqDTO;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingIssueResDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class TossBillingClient {

    private final WebClient webClient;


    public TossBillingClient(@Value("${toss.secret-key}") String secretKey) {
        String encoded = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        this.webClient = WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public BillingIssueResDTO issueBillingKey(String authKey, String customerKey) {
        BillingIssueReqDTO req = new BillingIssueReqDTO(authKey, customerKey);

        return webClient.post()
                .uri("/v1/billing/authorizations/issue")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(BillingIssueResDTO.class)
                .block();
    }

    public void deleteBillingKey(String billingKey) {
        webClient.delete()
                .uri("/v1/billing/{billingKey}", billingKey)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("[TOSS] BillingKey revoked. billingKey={}", billingKey);
    }

    // 자동 결제 승인
    public BillingApproveResDTO approveWithBillingKey(
            BillingAuthENT billingAuth,
            BigDecimal amount,
            String title,
            String idemKey,
            String paymentOrderId
    ) {


        BillingApproveReqDTO req = BillingApproveReqDTO.builder()
                .amount(amount)
                .customerKey(billingAuth.getCustomerKey())
                .orderId(paymentOrderId)
                .orderName(title)
                .build();

        return webClient.post()
                .uri("/v1/billing/{billingKey}", billingAuth.getBillingKey())
                .header("Idempotency-Key", idemKey)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(BillingApproveResDTO.class)
                .block();
    }
}
