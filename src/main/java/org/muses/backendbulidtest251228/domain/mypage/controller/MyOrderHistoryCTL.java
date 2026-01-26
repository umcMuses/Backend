package org.muses.backendbulidtest251228.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyOrderHistoryResDT;
import org.muses.backendbulidtest251228.domain.mypage.service.MyOrderHistorySRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "마이페이지 - 주문 결제", description = "크리에이터가 자신의 주문 및 결제를 전체/상세조회하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/orders")
public class MyOrderHistoryCTL {

    private final MyOrderHistorySRV myOrderHistorySRV;

    @Operation(summary = "내 결제 목록 조회", description = "내가 결제 및 후원한 목록을 조회")
    @GetMapping
    public ApiResponse<MyOrderHistoryResDT.OrderHistoryListResponse> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(myOrderHistorySRV.getMyOrders(userDetails));
    }

    @Operation(summary = "내 결제 목록 상세 조회", description = "내가 결제 및 후원한 목록을 상세조회")
    @GetMapping("/detail")
    public ApiResponse<MyOrderHistoryResDT.OrderHistoryDetailResponse> getMyOrderDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long orderId
    ) {
        return ApiResponse.success(myOrderHistorySRV.getMyOrderDetail(userDetails, orderId));
    }
}