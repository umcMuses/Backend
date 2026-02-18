package org.muses.backendbulidtest251228.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectResDT.*;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectReqDT;
import org.muses.backendbulidtest251228.domain.mypage.enums.QrStatus;
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


    @Operation(
            summary = "메이커 주문 QR 상태 변경",
            description = """
        메이커 명단에서 특정 주문의 QR 버튼 상태를 변경합니다.

        동작 조건:
        1. qrStatus가 NONE인 경우 → 수정 불가
        2. 프로젝트의 펀딩 상태가 SUCCESS(펀딩 성공)가 아닌 경우 → 수정 불가
        3. 펀딩 성공 상태인 경우:
           - ACTIVE → 해당 주문의 QR 티켓(UNUSED)을 모두 USED로 변경 (비활성화)
           - INACTIVE → 해당 주문의 QR 티켓(USED)을 모두 UNUSED로 변경 (활성화)

        ※ QR 티켓이 없는 주문은 수정할 수 없습니다.
        """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "QR 상태 변경 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (NONE 상태 또는 펀딩 성공 아님)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "내 프로젝트가 아님"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 또는 주문을 찾을 수 없음"
            )
    })
    @PostMapping("/creator-center/projects/{projectId}/makers/orderId/{orderId}/status/{qrStatus}")
    public ApiResponse<Void> changeQrStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long orderId,
            @PathVariable QrStatus qrStatus

    ) {

        creatorCenterProjectSRV.changeQrStatus(userDetails, projectId, orderId, qrStatus);
        return ApiResponse.success(null);
    }


    @Operation(summary = "정산", description = "프로젝트 정산 통계")
    @GetMapping("/creator-center/projects/{projectId}/settlement")
    public ApiResponse<org.muses.backendbulidtest251228.domain.mypage.dto.CreatorSettlementResDT> getSettlement(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(creatorCenterAnalyticsSRV.getSettlement(userDetails, projectId));
    }


}
