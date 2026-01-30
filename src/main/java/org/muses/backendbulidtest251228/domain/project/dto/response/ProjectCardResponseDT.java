package org.muses.backendbulidtest251228.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "프로젝트 카드 응답 DTO - 목록 조회 시 각 프로젝트 카드에 표시되는 정보")
@Getter
@Builder
@AllArgsConstructor
public class ProjectCardResponseDT {

    @Schema(description = "프로젝트 고유 ID", example = "1")
    private Long projectId;

    @Schema(description = "프로젝트 대표 이미지 URL (썸네일)")
    private String thumbnailUrl;

    @Schema(description = "프로젝트 제목", example = "인디밴드 단독 콘서트")
    private String title;

    @Schema(description = "목표 금액 대비 달성률 (%), 실시간 업데이트", example = "127")
    private Integer achieveRate;

    @Schema(description = "펀딩 마감 일시", example = "2025-03-01T23:59:59")
    private LocalDateTime deadline;

    @Schema(description = "펀딩 마감까지 남은 일수 (D-day), 음수면 마감됨", example = "15")
    private Long dDay;

    @Schema(
            description = "펀딩 진행 상태",
            example = "FUNDING",
            allowableValues = {"PREPARING", "SCHEDULED", "FUNDING", "SUCCESS", "FAIL", "CANCELLED"}
    )
    private String fundingStatus;

    @Schema(description = "오픈 예정 프로젝트 여부 (fundingStatus가 SCHEDULED면 true)", example = "false")
    private Boolean isScheduled;

    @Schema(description = "펀딩 시작(오픈) 일시, 오픈 예정 프로젝트에서 사용", example = "2025-02-01T00:00:00")
    private LocalDateTime opening;

    @Schema(description = "첨부파일 중 첫 번째 이미지 URL (없으면 null)")
    private String attachmentImageUrl;
}
