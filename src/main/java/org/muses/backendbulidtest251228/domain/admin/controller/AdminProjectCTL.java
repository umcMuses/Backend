package org.muses.backendbulidtest251228.domain.admin.controller;

import java.security.Principal;
import java.util.List;

import org.apache.ibatis.builder.BuilderException;
import org.muses.backendbulidtest251228.domain.admin.dto.AdminProjectDT;
import org.muses.backendbulidtest251228.domain.admin.enums.ProjectAuditStatus;
import org.muses.backendbulidtest251228.domain.admin.service.AdminProjectSRV;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectDetailResponseDT;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.muses.backendbulidtest251228.global.security.PrincipalDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 - 프로젝트 심사 및 관리", description = "관리자 - 프로젝트 심사 관련 API")
@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectCTL {

	private final AdminProjectSRV adminProjectSRV;

	@Operation(summary = "심사 프로젝트 목록 조회 API",
		description = "<p>메이커가 제출한 프로젝트 생성 요청 심사 API\nstatus 파라미터가 없을 시, 전체 조회됩니다.</p>")
	@GetMapping
	public ApiResponse<List<AdminProjectDT.ProjectAuditListResponse>> getProjects(
		@Parameter(description = "상태 필터 (DRAFT: 작성중, PENDING: 검토중, APPROVED: 승인됨, REJECTED: 반려됨)")
		@RequestParam(required = false) ProjectAuditStatus status,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		// 최신 생성 순으로 정렬
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<AdminProjectDT.ProjectAuditListResponse> resultPage = adminProjectSRV.getProjectAuditList(status, pageable);
		ApiResponse.PageInfo pageInfo = new ApiResponse.PageInfo(
			resultPage.getNumber(),
			resultPage.getSize(),
			resultPage.getTotalElements()
		);

		return ApiResponse.success(resultPage.getContent(), pageInfo);
	}

	@Operation(summary = "프로젝트 상세 조회(관리자용) API", description = "메이커가 작성한 프로젝트 제출 상세 내용을 확인합니다.")
	@GetMapping("/{projectId}")
	public ApiResponse<ProjectDetailResponseDT> getProjectDetail(
		@PathVariable Long projectId
	) {
		return ApiResponse.success(adminProjectSRV.getProjectDetail(projectId));
	}

	@Operation(summary = "프로젝트 승인/반려 처리 API", description = "제출된 프로젝트 상세 페이지 최하단 승인/반려 버튼을 누를시 호출됩니다.")
	@PatchMapping("/{projectId}/review")
	public ApiResponse<String> reviewProject(
		@PathVariable Long projectId,
		@RequestBody AdminProjectDT.AuditReviewRequest request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		if (principalDetails == null) {
			throw new BusinessException(ErrorCode.AUTH_REQUIRED);
		}
		Long adminId = principalDetails.getMemberId();
		adminProjectSRV.reviewProject(projectId, adminId, request);
		return ApiResponse.success("프로젝트 심사가 완료되었습니다. [관리자 ID:" + adminId + "]");
	}
}
