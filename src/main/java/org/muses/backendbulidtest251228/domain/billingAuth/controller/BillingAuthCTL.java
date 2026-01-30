package org.muses.backendbulidtest251228.domain.billingAuth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.domain.billingAuth.dto.BillingAuthIssueReqDTO;
import org.muses.backendbulidtest251228.domain.billingAuth.service.BillingAuthSRV;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BillingAuth", description = "빌링키(자동결제) 인증/발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing-auth")
public class BillingAuthCTL {

    private final BillingAuthSRV billingAuthService;


    //  successUrl로 돌아온 authKey를 프론트가 서버로 넘김 -> billingKey 발급
    // 빌링키 생성
    @Operation(
            summary = "빌링키 발급",
            description = "프론트에서 받은 authKey 기반으로 PG에 요청하여 billingKey를 발급한다."
    )
    @PostMapping("/issue")
    public ApiResponse<String> issueBillingKey(
            @Parameter(
                    description = "빌링 인증을 연결할 주문 ID",
                    required = true,
                    example = "34"
            )
            @RequestParam Long orderId,

            @Parameter(description = "빌링키 발급 요청 바디", required = true)
            @Valid @RequestBody BillingAuthIssueReqDTO req
    ) {


        billingAuthService.issueBillingKey(req, orderId);


        return ApiResponse.success("OK");
    }


}
