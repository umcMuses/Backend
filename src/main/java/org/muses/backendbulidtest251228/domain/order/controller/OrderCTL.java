package org.muses.backendbulidtest251228.domain.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.common.ApiResponse;
import org.muses.backendbulidtest251228.domain.order.dto.OrderCreateReqDTO;
import org.muses.backendbulidtest251228.domain.order.dto.OrderCreateResDTO;
import org.muses.backendbulidtest251228.domain.order.service.OrderSRV;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing-auth")
public class OrderCTL {

    private final OrderSRV orderSRV;


    // 프론트 라우팅 URL (성공)
    @Value("${muses.billing.success-url}")
    private String successUrl;

    // 프론트 라우팅 URL (실패)
    @Value("${muses.billing.fail-url}")
    private String failUrl;

    // 결제 버튼 누르면: customerKey + success/fail 내려주기
    @GetMapping("/prepare")
    public ResponseEntity<ApiResponse<OrderCreateResDTO>> createOrder(@Valid @RequestBody OrderCreateReqDTO req) {

        Long userId = 1L; // 임시 스프링 시큐리티 도입시 반영 필요


        OrderCreateResDTO resDTO = orderSRV.prepare(successUrl, failUrl, userId, req);

        return ResponseEntity.ok(ApiResponse.success(resDTO));

    }
}
