package org.muses.backendbulidtest251228.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "프로젝트 목록 응답 DTO - 검색/필터링 결과와 페이징 정보 포함")
@Getter
@Builder
@AllArgsConstructor
public class ProjectListResponseDT {

    @Schema(description = "프로젝트 카드 목록 (현재 페이지에 해당하는 프로젝트들)")
    private List<ProjectCardResponseDT> projects;

    @Schema(description = "검색 조건에 맞는 전체 프로젝트 개수", example = "42")
    private int totalCount;

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private int page;

    @Schema(description = "한 페이지당 프로젝트 개수", example = "10")
    private int size;

    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;

    @Schema(description = "다음 페이지 존재 여부 (true: 다음 페이지 있음)", example = "true")
    private boolean hasNext;
}
