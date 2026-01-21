package org.muses.backendbulidtest251228.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "프로젝트 카드 응답 DTO (리스트용)")
@Getter
@Builder
@AllArgsConstructor
public class ProjectCardResponseDTO {

    @Schema(description = "프로젝트 ID")
    private Long projectId;

    @Schema(description = "대표 이미지 URL")
    private String thumbnailUrl;

    @Schema(description = "프로젝트 제목")
    private String title;

    @Schema(description = "달성률 (%)")
    private Integer achieveRate;

    @Schema(description = "펀딩 마감일")
    private LocalDateTime deadline;

    @Schema(description = "남은 일수 (D-day)")
    private Long dDay;

    @Schema(description = "프로젝트 상태 (FUNDING, SCHEDULED, SUCCESS, FAIL)")
    private String fundingStatus;

    @Schema(description = "오픈 예정 여부")
    private Boolean isScheduled;

    @Schema(description = "펀딩 시작일 (오픈 예정용)")
    private LocalDateTime opening;
}
