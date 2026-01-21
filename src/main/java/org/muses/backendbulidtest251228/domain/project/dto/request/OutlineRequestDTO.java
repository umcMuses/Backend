package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.project.enums.AgeLimit;
import org.muses.backendbulidtest251228.domain.project.enums.Region;

import java.util.List;

@Schema(description = "1단계: 개요 저장 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutlineRequestDTO {

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
}
