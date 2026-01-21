package org.muses.backendbulidtest251228.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "프로젝트 목록 응답 DTO (페이징 포함)")
@Getter
@Builder
@AllArgsConstructor
public class ProjectListResponseDTO {

    @Schema(description = "프로젝트 카드 목록")
    private List<ProjectCardResponseDTO> projects;

    @Schema(description = "전체 개수")
    private int totalCount;

    @Schema(description = "현재 페이지 (0부터 시작)")
    private int page;

    @Schema(description = "페이지 크기")
    private int size;

    @Schema(description = "전체 페이지 수")
    private int totalPages;

    @Schema(description = "다음 페이지 존재 여부")
    private boolean hasNext;
}
