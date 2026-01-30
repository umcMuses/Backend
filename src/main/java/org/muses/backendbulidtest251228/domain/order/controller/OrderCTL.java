package org.muses.backendbulidtest251228.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.domain.order.dto.OrderCreateReqDT;
import org.muses.backendbulidtest251228.domain.order.dto.OrderCreateResDT;
import org.muses.backendbulidtest251228.domain.order.service.OrderSRV;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "주문 생성/취소 API")
public class OrderCTL {

    private final OrderSRV orderSRV;

    private final MemberRepo memberRepo;


    // 프론트 라우팅 URL (성공)
    @Value("${muses.billing.success-url}")
    private String successUrl;

    // 프론트 라우팅 URL (실패)
    @Value("${muses.billing.fail-url}")
    private String failUrl;

    // 결제 버튼 누르면: customerKey + success/fail 내려주기
    @Operation(
            summary = "주문 준비 (RESERVED 주문 생성)",
            description = """
                응원하기 버튼 클릭 시 주문(RESERVED)을 생성합니다.
                결제 진행에 필요한 customerKey와 성공/실패 URL을 반환합니다.
                """
         )
    @PostMapping("/prepare")
    public ApiResponse<OrderCreateResDT> createOrder(
            @Valid @org.springframework.web.bind.annotation.RequestBody OrderCreateReqDT req,
            @AuthenticationPrincipal UserDetails userDetails
    ) {


        Member member = memberRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID, "유효하지 않은 인증 정보입니다."));

        Long userId = member.getId();


        OrderCreateResDT resDTO = orderSRV.prepare(successUrl, failUrl, userId, req);

        return ApiResponse.success(resDTO);

    }

    /*@Operation(
            summary = "주문 상세 취소 (부분 취소)",
            description = "주문 상세의 수량을 차감하거나 전체 삭제하고, 주문 금액이 0원이 되면 주문을 취소합니다."
    )
    @DeleteMapping("/cancel")
    public ApiResponse<?> cancelOrder(@Parameter(description = "취소할 주문 상세 ID", example = "1")
                                                          @RequestParam("orderItemId") Long orderItemId,

                                                      @Parameter(description = "취소할 수량", example = "1")
                                                          @RequestParam("quantity") Integer qty){

        orderSRV.cancel(orderItemId, qty);


        return ApiResponse.success("OK");
    }*/


    @Operation(
            summary = "주문 취소 (전체 취소)",
            description = "해당 주문 전체를 취소하고 빌링키를 삭제합니다"
    )
    @DeleteMapping("/cancel/all/{orderId}")
    public ApiResponse<?> cancelAllOrder(@Parameter(description = "취소할 주문 ID", example = "1")
                                             @PathVariable("orderId") Long orderId){

        orderSRV.cancel(orderId);


        return ApiResponse.success("OK");
    }

}
