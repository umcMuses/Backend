package org.muses.backendbulidtest251228.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyOrderHistoryResDT;
import org.muses.backendbulidtest251228.domain.mypage.service.MyOrderHistorySRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/orders")
public class MyOrderHistoryCTL {

    private final MyOrderHistorySRV myOrderHistorySRV;

    // 전체 후원(결제) 내역 조회
    @GetMapping
    public ApiResponse<MyOrderHistoryResDT.OrderHistoryListResponse> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(myOrderHistorySRV.getMyOrders(userDetails));
    }

    // 후원(결제) 내역 상세 조회
    @GetMapping("/detail")
    public ApiResponse<MyOrderHistoryResDT.OrderHistoryDetailResponse> getMyOrderDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long orderId
    ) {
        return ApiResponse.success(myOrderHistorySRV.getMyOrderDetail(userDetails, orderId));
    }
}