package org.muses.backendbulidtest251228.domain.billingAuth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.common.ApiResponse;
import org.muses.backendbulidtest251228.domain.billingAuth.dto.BillingAuthIssueReqDTO;
import org.muses.backendbulidtest251228.domain.billingAuth.service.BillingAuthSRV;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing-auth")
public class BillingAuthCTL {

    private final BillingAuthSRV billingAuthService;


    //  successUrl로 돌아온 authKey를 프론트가 서버로 넘김 -> billingKey 발급
    // 빌링키 생성
    @PostMapping("/issue")
    public ResponseEntity<ApiResponse<String>> issueBillingKey(
            @RequestParam Long orderId,
            @Valid @RequestBody BillingAuthIssueReqDTO req) {


        billingAuthService.issueBillingKey(req, orderId);


        return ResponseEntity.ok(ApiResponse.success("OK"));
    }


}
