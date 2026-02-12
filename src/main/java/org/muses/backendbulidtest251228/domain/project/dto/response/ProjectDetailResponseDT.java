package org.muses.backendbulidtest251228.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.muses.backendbulidtest251228.domain.project.enums.AgeLimit;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.enums.Region;
import org.muses.backendbulidtest251228.domain.storage.dto.AttachmentResponseDT;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "프로젝트 상세 응답 DTO - 프로젝트의 모든 정보를 포함 (임시저장 데이터 통합 조회)")
@Getter
@Builder
@AllArgsConstructor
public class ProjectDetailResponseDT {

    @Schema(description = "프로젝트 고유 ID", example = "1")
    private Long projectId;

    @Schema(
            description = "프로젝트 심사/진행 상태",
            example = "FUNDING",
            allowableValues = {"DRAFT", "PENDING", "APPROVED", "REJECTED", "FUNDING", "SUCCESS", "FAIL", "CANCELLED"}
    )
    private String status;

    @Schema(description = "현재까지 저장 완료된 단계 (1~5), 0이면 아직 저장 안함", example = "3")
    private Integer lastSavedStep;

    // ========== 1단계: 개요 ==========
    @Schema(description = "[1단계] 프로젝트 제목", example = "인디밴드 단독 콘서트")
    private String title;

    @Schema(description = "[1단계] 프로젝트 간략 소개 (한 줄 설명)", example = "첫 번째 단독 콘서트입니다")
    private String description;

    @Schema(description = "[1단계] 대표 이미지 URL (썸네일)")
    private String thumbnailUrl;

    @Schema(description = "[1단계] 프로젝트 태그 목록 (검색용)", example = "[\"음악\", \"콘서트\", \"인디밴드\"]")
    private List<String> tags;

    @Schema(description = "[1단계] 연령 제한 (ALL: 전체이용가, ADULT: 19세 이상)", example = "ALL")
    private AgeLimit ageLimit;

    @Schema(description = "[1단계] 공연/행사 지역", example = "SEOUL")
    private Region region;

    // ========== 2단계: 펀딩 ==========
    @Schema(description = "[2단계] 목표 펀딩 금액 (원)", example = "5000000")
    private BigDecimal targetAmount;

    @Schema(description = "[2단계] 펀딩 시작(오픈) 일시", example = "2025-02-01T00:00:00")
    private LocalDateTime opening;

    @Schema(description = "[2단계] 펀딩 마감 일시", example = "2025-03-01T23:59:59")
    private LocalDateTime deadline;

    @Schema(
            description = "[2단계] 펀딩 진행 상태",
            example = "FUNDING",
            allowableValues = {"PREPARING", "SCHEDULED", "FUNDING", "SUCCESS", "FAIL", "CANCELLED"}
    )
    private FundingStatus fundingStatus;

    // ========== 3단계: 리워드 ==========
    @Schema(description = "[3단계] 리워드(후원 상품) 목록")
    private List<RewardResponseDT> rewards;

    // ========== 4단계: 스토리 ==========
    @Schema(description = "[4단계] 프로젝트 스토리 본문 (HTML 형식, 에디터 데이터)", example = "<h1>프로젝트 소개</h1>")
    private String storyHtml;

    @Schema(description = "[4단계] 환불/취소 정책 안내", example = "공연 7일 전까지 전액 환불 가능, 이후 환불 불가")
    private String refundPolicy;

    @Schema(description = "[4단계] 첨부파일 목록")
    private List<AttachmentResponseDT> attachments;

    // ========== 5단계: 정보 ==========
    @Schema(description = "[5단계] 크리에이터(진행자) 이름", example = "이유리")
    private String creatorName;

    @Schema(description = "[5단계] 크리에이터(진행자) 닉네임", example = "MusicLover")
    private String creatorNickName;

    @Schema(description = "[5단계] 크리에이터 프로필 이미지 URL")
    private String hostProfileImg;

    @Schema(description = "[5단계] 크리에이터 연락처", example = "010-1234-5678")
    private String hostPhone;

    @Schema(description = "[5단계] 크리에이터 소개", example = "보컬을 맡고 있습니다")
    private String hostBio;

    @Schema(description = "[5단계] 담당자 이름", example = "김철수")
    private String managerName;

    @Schema(description = "[5단계] 담당자 연락처", example = "010-9876-5432")
    private String managerPhone;

    @Schema(description = "[5단계] 정산 서류 목록")
    private List<AttachmentResponseDT> documents;

    @Schema(description = "[5단계] 메이커 서류 목록")
    private List<AttachmentResponseDT> makerDocuments;

    // ========== 통계 ==========
    @Schema(description = "[통계] 목표 금액 대비 달성률 (%), 실시간 업데이트", example = "127")
    private Integer achieveRate;

    @Schema(description = "[통계] 현재까지 후원한 서포터 수", example = "89")
    private Integer supporterCount;

    @Schema(description = "[통계] 프로젝트 최초 생성 일시", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "[통계] 프로젝트 마지막 수정 일시", example = "2025-01-20T14:20:00")
    private LocalDateTime updatedAt;
}
