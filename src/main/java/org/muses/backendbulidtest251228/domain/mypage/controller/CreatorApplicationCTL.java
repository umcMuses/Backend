package org.muses.backendbulidtest251228.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplyReqDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplyResDT;
import org.muses.backendbulidtest251228.domain.mypage.service.CreatorApplicationSRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "마이페이지 - 크리에이터 전환", description = "메이커 > 크리에이터 전환 신청")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/creators/applications")
public class CreatorApplicationCTL {

    private final CreatorApplicationSRV creatorApplicationSRV;

    @Operation(summary = "크리에이터 전환 신청", description = "로그인한 사용자가 크리에이터 전환을 신청")
    @PostMapping
    public ApiResponse<CreatorApplyResDT> apply(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreatorApplyReqDT req
    ) {
        return ApiResponse.success(
                creatorApplicationSRV.apply(userDetails, req)
        );
    }

    @Operation(summary = "내 크리에이터 전환 신청 조회", description = "로그인한 사용자의 크리에이터 전환 신청 상태를 조회")
    @GetMapping("/me")
    public ApiResponse<CreatorApplyResDT> myApplication(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(
                creatorApplicationSRV.getMyApplication(userDetails)
        );
    }
}
