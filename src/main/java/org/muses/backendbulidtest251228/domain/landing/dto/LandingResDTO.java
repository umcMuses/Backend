package org.muses.backendbulidtest251228.domain.landing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.muses.backendbulidtest251228.domain.project.enums.Region;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "LandingResDTO",
        description = "랜딩 페이지 프로젝트 카드 응답 DTO")
@Getter
@Builder
@AllArgsConstructor
public class LandingResDTO {

    @Schema(description = "프로젝트 고유 ID", example = "1")
    private Long projectId;

    @Schema(
            description = "프로젝트 대표 이미지 URL (썸네일)",
            example = "https://cdn.muses.com/project/thumbnail.png"
    )
    private String thumbnailUrl;

    @Schema(description = "프로젝트 제목", example = "인디밴드 단독 콘서트")
    private String title;

    @Schema(description = "목표 금액 대비 달성률 (%)", example = "127")
    private Integer achieveRate;

    @Schema(
            description = "펀딩 마감 일시 (ISO-8601)",
            example = "2025-03-01T23:59:59"
    )
    private LocalDateTime deadline;

    @Schema(
            description = "펀딩 마감까지 남은 일수 (D-day, 음수면 마감됨)",
            example = "15"
    )
    private Long dDay;

    @Schema(
            description = "펀딩 진행 상태",
            example = "FUNDING",
            allowableValues = {
                    "PREPARING",
                    "SCHEDULED",
                    "FUNDING",
                    "CLOSING",
                    "SUCCESS",
                    "FAIL"
            }
    )
    private String fundingStatus;

    @Schema(
            description = "프로젝트 지역",
            example = "SEOUL"
    )
    private Region region;

    @Schema(
            description = "프로젝트 태그 목록",
            example = "[\"콘서트\", \"인디\", \"라이브\"]"
    )
    private List<String> tags;


}