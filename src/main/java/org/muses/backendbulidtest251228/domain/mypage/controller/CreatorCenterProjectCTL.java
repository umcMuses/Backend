package org.muses.backendbulidtest251228.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectDT.*;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectReqDT;
import org.muses.backendbulidtest251228.domain.mypage.service.CreatorCenterProjectSRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "크리에이터 센터 - 프로젝트", description = "크리에이터가 자신의 프로젝트를 조회/수정하는 API")

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/creators")
public class CreatorCenterProjectCTL {

    private final CreatorCenterProjectSRV creatorCenterProjectSRV;
    private final org.muses.backendbulidtest251228.domain.mypage.service.CreatorCenterAnalyticsSRV creatorCenterAnalyticsSRV;

    @Operation(summary = "내 프로젝트 목록 조회", description = "로그인한 크리에이터가 생성한 프로젝트 목록을 조회")
    @GetMapping("/me/projects")
    public ApiResponse<MyProjectListResponse> getMyProjects(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(creatorCenterProjectSRV.getMyProjects(userDetails));
    }

    @Operation(summary = "내 프로젝트 설정 조회(수정 누르기 전)", description = "로그인한 크리에이터가 생성한 프로젝트를 설정을 조회")
    @GetMapping("/creator-center/projects/{projectId}/setting")
    public ApiResponse<ProjectSettingsResponse> getProjectSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(creatorCenterProjectSRV.getProjectSettings(userDetails, projectId));
    }

    @Operation(summary = "내 프로젝트 설정 수정", description = "로그인한 크리에이터가 생성한 프로젝트를 설정을 수정")
    @PatchMapping("/creator-center/projects/{projectId}/details")
    public ApiResponse<ProjectSettingsResponse> updateProjectSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long projectId,
            @RequestBody CreatorCenterProjectReqDT.UpdateProjectSettingsRequest request
    ) {
        return ApiResponse.success(creatorCenterProjectSRV.updateProjectSettings(userDetails, projectId, request));
    }

    @Operation(summary = "내 프로젝트 메이커 명단 조회", description = "로그인한 크리에이터가 내 프로젝트의 메이커 명단을 조회")
    @GetMapping("/creator-center/projects/{projectId}/makers")
    public ApiResponse<MakerListResponse> getProjectMakers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(creatorCenterProjectSRV.getProjectMakers(userDetails, projectId));
    }

    @Operation(summary = "크리에이터 요약 정보", description = "총 후원금, 진행중 프로젝트 수")
    @GetMapping("/me/summary")
    public ApiResponse<org.muses.backendbulidtest251228.domain.mypage.dto.CreatorSummaryResDT> getMySummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(creatorCenterAnalyticsSRV.getMySummary(userDetails));
    }

    @Operation(summary = "대시보드", description = "프로젝트 대시보드 통계")
    @GetMapping("/creator-center/projects/{projectId}/dashboard")
    public ApiResponse<org.muses.backendbulidtest251228.domain.mypage.dto.CreatorDashboardResDT> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(creatorCenterAnalyticsSRV.getProjectDashboard(userDetails, projectId));
    }

}
