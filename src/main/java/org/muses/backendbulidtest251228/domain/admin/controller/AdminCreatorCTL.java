package org.muses.backendbulidtest251228.domain.admin.controller;

import org.muses.backendbulidtest251228.domain.admin.dto.AdminCreatorDT;
import org.muses.backendbulidtest251228.domain.admin.service.AdminCreatorSRVI;
import org.muses.backendbulidtest251228.domain.mypage.enums.ApplicationStatus;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.muses.backendbulidtest251228.global.security.PrincipalDetails;
import org.springframework.security.access.prepost.PreAuthorize;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 - 크리에이터 전환 심사", description = "크리에이터 전환 신청 검토 및 승인/반려 처리")
@RestController
@RequestMapping("/api/admin/creators")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCreatorCTL {

	private final AdminCreatorSRVI adminCreatorSRVI;

	@Operation(
		summary = "크리에이터 전환 신청 목록 조회",
		description = """
			status로 필터링 가능(미입력시 전체 조회), 최신순 정렬
			- PENDING: 대기중 (심사 대기)
			- APPROVED: 승인됨
			- REJECTED: 반려됨
			"""
	)
	@GetMapping("/applications")
	public ApiResponse<AdminCreatorDT.ApplicationListResponse> getApplicationList(
		@Parameter(description = "상태 필터")
		@RequestParam(required = false)ApplicationStatus status,
		@Parameter(description = "페이지 번호(default: 0)")
		@RequestParam(defaultValue = "0") int page,
		@Parameter(description = "페이지 크기")
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		validateAdmin(principalDetails);
		AdminCreatorDT.ApplicationListResponse response =
			adminCreatorSRVI.getApplicationList(status, page, size);
		return ApiResponse.success(response);
	}

	@Operation(summary = "서류 전체 조회 API")
	@GetMapping("/applications/{appId}/documents")
	public ApiResponse<AdminCreatorDT.DocumentListResponse> getApplicationDocuments(
		@Parameter(description = "신청 ID", required = true)
		@PathVariable Long appId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		validateAdmin(principalDetails);
		AdminCreatorDT.DocumentListResponse response =
			adminCreatorSRVI.getApplicationDocuments(appId);
		return ApiResponse.success(response);
	}

	@Operation(summary = "서류 개별 이미지 조회 API", description = "특정 서류 이미지 버튼 클릭시 해당 서류만 팝업 형식으로 조회합니다.")
	@GetMapping("/applications/{appId}/documents/{docType}")
	public ApiResponse<AdminCreatorDT.SingleDocumentResponse> getApplicationDocumentImg(
		@Parameter(description = "신청 ID", required = true)
		@PathVariable Long appId,
		@Parameter(description = "서류 유형", required = true)
		@PathVariable String docType,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		validateAdmin(principalDetails);
		AdminCreatorDT.SingleDocumentResponse response =
			adminCreatorSRVI.getSingleDocument(appId, docType);
		return ApiResponse.success(response);
	}

	@Operation(summary = "크리에이터 전환 승인/반려 API",
		description = """
			한 번 승인/반려 시 수정이 불가능합니다.
			
			- PENDING 상태의 신청만 처리 가능
			- 승인(APPROVED) 시 Role이 CREATOR로 변경됨
			- 반려(REJECTED) 시 Role 유지
			"""
	)
	@PatchMapping("/applications/review")
	public ApiResponse<AdminCreatorDT.ReviewResponse> updateApplicationReview(
		@Valid @RequestBody AdminCreatorDT.ReviewRequest request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long adminId = validateAdmin(principalDetails);
		AdminCreatorDT.ReviewResponse response =
			adminCreatorSRVI.reviewApplication(adminId, request);
		return ApiResponse.success(response);
	}

	// 관리자 ID 반환
	private Long validateAdmin(PrincipalDetails principalDetails) {
		if (principalDetails == null) {
			throw new BusinessException(ErrorCode.AUTH_REQUIRED);
		}
		return principalDetails.getMemberId();
	}

}
