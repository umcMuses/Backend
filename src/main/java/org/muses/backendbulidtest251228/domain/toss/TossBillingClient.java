package org.muses.backendbulidtest251228.domain.toss;

import lombok.extern.slf4j.Slf4j;
import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingApproveReqDT;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingApproveResDT;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingIssueReqDT;
import org.muses.backendbulidtest251228.domain.toss.dto.BillingIssueResDT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TossBillingClient {

    private final WebClient webClient;


    public TossBillingClient(@Value("${toss.secret-key}") String secretKey) {
        String encoded = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));



        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS)));


        this.webClient = WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public BillingIssueResDT issueBillingKey(String authKey, String customerKey) {
        BillingIssueReqDT req = new BillingIssueReqDT(authKey, customerKey);

        return webClient.post()
                .uri("/v1/billing/authorizations/issue")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(BillingIssueResDT.class)
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
    public BillingApproveResDT approveWithBillingKey(
            BillingAuthENT billingAuth,
            BigDecimal amount,
            String title,
            String idemKey,
            String paymentOrderId
    ) {


        BillingApproveReqDT req = BillingApproveReqDT.builder()
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
                .bodyToMono(BillingApproveResDT.class)
                .block();
    }
}
