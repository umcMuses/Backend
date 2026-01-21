package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "4단계: 스토리 저장 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoryRequestDTO {

    @Schema(description = "스토리 HTML 본문", example = "<h1>프로젝트 소개</h1><p>내용...</p>")
    private String storyHtml;

    @Schema(description = "환불 정책", example = "공연 7일 전까지 전액 환불 가능")
    private String refundPolicy;
}
