package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.project.enums.AgeLimit;
import org.muses.backendbulidtest251228.domain.project.enums.Region;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "프로젝트 기본정보 요청 DTO (MUS-013)")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectBasicRequestDTO {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "프로젝트 제목", example = "인디밴드 콘서트")
    private String title;

    @Schema(description = "간략 소개", example = "신나는 인디밴드 공연입니다")
    private String description;

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.jpg")
    private String thumbnailUrl;

    @Schema(description = "태그 목록", example = "[\"음악\", \"콘서트\", \"인디\"]")
    private List<String> tags;

    @Schema(description = "19세 여부", example = "ALL")
    private AgeLimit ageLimit;

    @Schema(description = "지역", example = "SEOUL")
    private Region region;

    @Schema(description = "목표 금액", example = "1000000")
    private BigDecimal targetAmount;

    @Schema(description = "마감일", example = "2025-03-01T23:59:59")
    private LocalDateTime deadline;
}
