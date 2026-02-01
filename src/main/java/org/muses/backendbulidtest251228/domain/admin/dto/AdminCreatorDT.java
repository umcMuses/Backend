package org.muses.backendbulidtest251228.domain.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminCreatorDT {

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Schema(description = "크리에이터 전환 신청 목록 아이템")
	public static class ApplicationListItem {
		@Schema(description = "신청 ID", example = "12")
		private Long applicationId;
		@Schema(description = "회원 ID", example = "5")
		private Long memberId;
		@Schema(description = "이름", example = "김민지")
		private String name;
		@Schema(description = "크리에이터 유형", example = "INDIVIDUAL")
		private String creatorType;
		@Schema(description = "크리에이터 유형 설명", example = "개인")
		private String creatorTypeDescription;
		@Schema(description = "신청 상태", example = "PENDING")
		private String status;
		@Schema(description = "신청 상태 설명", example = "대기중")
		private String statusDescription;
		@Schema(description = "신청 일시", example = "2026-01-30T10:30:00")
		private LocalDateTime createdAt;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Schema(description = "크리에이터 전환 신청 목록")
	public static class ApplicationListResponse {
		@Schema(description = "신청 목록")
		private List<ApplicationListItem> items;
		@Schema(description = "전체 개수")
		private long totalCount;
		@Schema(description = "현재 페이지")
		private int page;
		@Schema(description = "페이지 크기")
		private int size;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "크리에이터 전환 승인/반려 요청")
	public static class ReviewRequest {
		@NotNull
		@Schema(description = "신청 ID", example = "12")
		private Long applicationId;
		@NotBlank
		@Pattern(regexp = "APPROVED|REJECTED")
		@Schema(description = "처리 상태 (APPROVED/REJECTED)", example = "APPROVED")
		private String status;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Schema(description = "크리에이터 전환 승인/반려 응답")
	public static class ReviewResponse {
		@Schema(description = "신청 ID", example = "12")
		private Long applicationId;
		@Schema(description = "회원 ID", example = "5")
		private Long memberId;
		@Schema(description = "처리 상태", example = "APPROVED")
		private String status;
		@Schema(description = "처리 상태 설명", example = "승인됨")
		private String statusDescription;
		@Schema(description = "처리 일시", example = "2026-01-30T15:30:00")
		private LocalDateTime processedAt;
		@Schema(description = "처리 관리자 ID", example = "1")
		private Long adminId;
	}

	/**
	 *  === 크리에이터 전환 서류 목록/이미지 조회 DT ===
	 */

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Schema(description = "제출 서류 정보")
	public static class DocumentItem {
		@Schema(description = "서류 ID", example = "1")
		private Long docId;
		@Schema(description = "서류 유형 설명", example = "신분증")
		private String docTypeDescription;
		@Schema(description = "첨부파일 ID", example = "18")
		private Long attachmentId;
		@Schema(description = "파일 URL", example = "https://bucket.s3.region.amazonaws.com/...")
		private String fileUrl;
		@Schema(description = "확장자", example = "jpg")
		private String extension;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Schema(description = "신청 서류 목록 응답")
	public static class DocumentListResponse {
		@Schema(description = "신청 ID", example = "12")
		private Long applicationId;
		@Schema(description = "회원 이름", example = "김철수")
		private String memberName;
		@Schema(description = "크리에이터 유형", example = "INDIVIDUAL")
		private String creatorType;
		@Schema(description = "서류 목록")
		private List<DocumentItem> documents;
		@Schema(description = "필요 서류 목록", example = "[\"ID_CARD\", \"BANKBOOK\"]")
		private List<String> requiredDocs;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Schema(description = "개별 서류 조회 응답 (팝업용)")
	public static class SingleDocumentResponse {
		@Schema(description = "신청 ID", example = "12")
		private Long applicationId;
		@Schema(description = "서류 유형", example = "ID_CARD")
		private String docType;
		@Schema(description = "서류 유형 설명", example = "신분증")
		private String docTypeDescription;
		@Schema(description = "파일 URL", example = "https://bucket.s3.region.amazonaws.com/...")
		private String fileUrl;
		@Schema(description = "원본 파일명", example = "id_card.jpg")
		private String originalFilename;
		@Schema(description = "확장자", example = "jpg")
		private String extension;
		@Schema(description = "제출 여부", example = "true")
		private boolean submitted;
	}


}
