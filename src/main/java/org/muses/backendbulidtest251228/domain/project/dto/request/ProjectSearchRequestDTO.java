package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "프로젝트 검색 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSearchRequestDTO {

    @Schema(description = "지역 필터 (SEOUL, GYEONGGI, BUSAN 등)", example = "SEOUL")
    private String region;

    @Schema(description = "펀딩 상태 필터 (FUNDING, SCHEDULED, SUCCESS, FAIL)", example = "FUNDING")
    private String fundingStatus;

    @Schema(description = "태그 검색", example = "음악")
    private String tag;

    @Schema(description = "키워드 검색 (제목, 설명)", example = "콘서트")
    private String keyword;

    @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
    private Integer page = 0;

    @Schema(description = "페이지 크기", example = "10")
    private Integer size = 10;
}
