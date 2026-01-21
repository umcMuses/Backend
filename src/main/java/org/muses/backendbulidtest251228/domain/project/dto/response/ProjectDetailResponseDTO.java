package org.muses.backendbulidtest251228.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.muses.backendbulidtest251228.domain.project.enums.AgeLimit;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.enums.FundingType;
import org.muses.backendbulidtest251228.domain.project.enums.Region;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "프로젝트 상세 응답 DTO")
@Getter
@Builder
@AllArgsConstructor
public class ProjectDetailResponseDTO {

    @Schema(description = "프로젝트 ID")
    private Long projectId;

    @Schema(description = "프로젝트 상태 (DRAFT, PENDING, APPROVED, REJECTED, FUNDING, SUCCESS, FAIL)")
    private String status;

    @Schema(description = "현재 저장 단계 (1~5)")
    private Integer lastSavedStep;

    // ========== 1단계: 개요 ==========
    @Schema(description = "프로젝트 제목")
    private String title;

    @Schema(description = "간략 소개")
    private String description;

    @Schema(description = "대표 이미지 URL")
    private String thumbnailUrl;

    @Schema(description = "태그 목록")
    private List<String> tags;

    @Schema(description = "19세 여부")
    private AgeLimit ageLimit;

    @Schema(description = "지역")
    private Region region;

    // ========== 2단계: 펀딩 ==========
    @Schema(description = "목표 금액")
    private BigDecimal targetAmount;

    @Schema(description = "펀딩 시작일")
    private LocalDateTime opening;

    @Schema(description = "펀딩 마감일")
    private LocalDateTime deadline;

    @Schema(description = "펀딩 방식")
    private FundingType fundingType;

    @Schema(description = "펀딩 상태")
    private FundingStatus fundingStatus;

    // ========== 3단계: 리워드 ==========
    @Schema(description = "리워드 목록")
    private List<RewardResponseDTO> rewards;

    // ========== 4단계: 스토리 ==========
    @Schema(description = "스토리 HTML 본문")
    private String storyHtml;

    @Schema(description = "환불 정책")
    private String refundPolicy;

    // ========== 5단계: 정보 ==========
    @Schema(description = "진행자 프로필 이미지")
    private String hostProfileImg;

    @Schema(description = "진행자 연락처")
    private String hostPhone;

    @Schema(description = "진행자 소개")
    private String hostBio;

    // ========== 통계 ==========
    @Schema(description = "달성률 (%)")
    private Integer achieveRate;

    @Schema(description = "후원자 수")
    private Integer supporterCount;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;
}
