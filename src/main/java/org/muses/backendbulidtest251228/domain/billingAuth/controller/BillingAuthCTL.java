package org.muses.backendbulidtest251228.domain.billingAuth.controller;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.common.ApiResponse;
import org.muses.backendbulidtest251228.domain.billingAuth.dto.BillingAuthPrepareResDTO;
import org.muses.backendbulidtest251228.domain.billingAuth.service.BillingAuthSRV;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing-auth")
public class BillingAuthCTL {

    private final BillingAuthSRV billingAuthService;


    // 프론트 라우팅 URL (성공)
    @Value("${muses.billing.success-url}")
    private String successUrl;

    // 프론트 라우팅 URL (실패)
    @Value("${muses.billing.fail-url}")
    private String failUrl;

    // 1) 후원 버튼 누르면: customerKey + success/fail 내려주기
    @GetMapping("/auth/prepare")
    public ResponseEntity<ApiResponse<BillingAuthPrepareResDTO>> prepare() {

        BillingAuthPrepareResDTO res =
                billingAuthService.prepare(successUrl, failUrl);

        return ResponseEntity.ok(ApiResponse.success(res));

    }


}
