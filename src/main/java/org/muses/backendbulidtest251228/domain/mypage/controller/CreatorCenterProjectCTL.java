package org.muses.backendbulidtest251228.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectDT.*;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectReqDT;
import org.muses.backendbulidtest251228.domain.mypage.service.CreatorCenterProjectSRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/creators")
public class CreatorCenterProjectCTL {

    private final CreatorCenterProjectSRV creatorCenterProjectSRV;

    // 8번 화면: 내 프로젝트 리스트
    @GetMapping("/me/projects")
    public ApiResponse<MyProjectListResponse> getMyProjects(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ApiResponse.success(creatorCenterProjectSRV.getMyProjects(userDetails));
    }

    //  프로젝트 설정 조회
    @GetMapping("/creator-center/projects/{projectId}/setting")
    public ApiResponse<ProjectSettingsResponse> getProjectSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(creatorCenterProjectSRV.getProjectSettings(userDetails, projectId));
    }

    // 상단 “프로젝트 정보 수정” PATCH
    @PatchMapping("/creator-center/projects/{projectId}/details")
    public ApiResponse<ProjectSettingsResponse> updateProjectSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long projectId,
            @RequestBody CreatorCenterProjectReqDT.UpdateProjectSettingsRequest request
    ) {
        return ApiResponse.success(creatorCenterProjectSRV.updateProjectSettings(userDetails, projectId, request));
    }

    // 4~5번 화면: 메이커 명단
    @GetMapping("/creator-center/projects/{projectId}/makers")
    public ApiResponse<MakerListResponse> getProjectMakers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(creatorCenterProjectSRV.getProjectMakers(userDetails, projectId));
    }
}
