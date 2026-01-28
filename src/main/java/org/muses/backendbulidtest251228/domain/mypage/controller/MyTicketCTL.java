package org.muses.backendbulidtest251228.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyTicketResDT;
import org.muses.backendbulidtest251228.domain.mypage.service.MyPageTicketQuerySRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "마이페이지 - 내 티켓", description = "로그인한 사용자의 티켓 리스트 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/tickets")
public class MyTicketCTL {

    private final MyPageTicketQuerySRV myPageTicketQueryService;

    @Operation(summary = "내 티켓 리스트 조회", description = "로그인한 사용자의 티켓(주문 기반) 목록 조회")
    @GetMapping
    public ApiResponse<List<MyTicketResDT>> myTickets(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(myPageTicketQueryService.getMyTickets(userDetails));
    }
}
