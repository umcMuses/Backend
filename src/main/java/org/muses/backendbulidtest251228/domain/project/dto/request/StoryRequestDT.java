package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "4단계: 스토리 저장 요청 DTO - 프로젝트 상세 소개 및 환불 정책")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoryRequestDT {

    @Schema(
            description = "프로젝트 스토리 본문 (HTML 형식, 에디터에서 작성한 내용 그대로 저장)",
            example = "<h1>프로젝트 소개</h1><p>2026년에 결성되어...</p><h2>공연 정보</h2><p>일시: 2026년 1월 1일</p>"
    )
    private String storyHtml;

    @Schema(
            description = "환불/취소 정책 안내 (후원자에게 표시됨)",
            example = "공연 7일 전까지 전액 환불 가능합니다."
    )
    private String refundPolicy;
}
