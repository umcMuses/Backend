package org.muses.backendbulidtest251228.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "프로젝트 좋아요 응답 DTO - 좋아요 상태 및 총 좋아요 수 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectLikeResponseDT {

    @Schema(description = "프로젝트 고유 ID", example = "1")
    private Long projectId;

    @Schema(
            description = "현재 로그인한 유저의 좋아요 여부 (true: 좋아요 누름, false: 좋아요 안 누름). 비로그인 시 항상 false",
            example = "true"
    )
    private Boolean liked;

    @Schema(description = "해당 프로젝트의 총 좋아요 수", example = "42")
    private Long likeCount;
}
