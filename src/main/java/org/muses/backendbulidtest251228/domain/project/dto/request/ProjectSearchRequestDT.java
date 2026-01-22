package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "프로젝트 검색/필터링 요청 DTO - 모든 필터는 선택사항이며, 복합 조건 검색 가능")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSearchRequestDT {

    @Schema(
            description = "지역 필터 (미입력 시 전체 지역)",
            example = "SEOUL",
            allowableValues = {"SEOUL", "GYEONGGI", "BUSAN", "DAEGU", "INCHEON", "GWANGJU", "DAEJEON", "ULSAN", "SEJONG", "GANGWON", "CHUNGBUK", "CHUNGNAM", "JEONBUK", "JEONNAM", "GYEONGBUK", "GYEONGNAM", "JEJU"}
    )
    private String region;

    @Schema(
            description = "펀딩 상태 필터 (미입력 시 전체 상태)",
            example = "FUNDING",
            allowableValues = {"PREPARING", "SCHEDULED", "FUNDING", "SUCCESS", "FAIL", "CANCELLED"}
    )
    private String fundingStatus;

    @Schema(
            description = "태그 검색 (부분 일치, 예: '음악' 입력 시 '음악', '음악회' 등 모두 검색)",
            example = "음악"
    )
    private String tag;

    @Schema(
            description = "키워드 검색 (제목과 설명에서 부분 일치 검색)",
            example = "콘서트"
    )
    private String keyword;

    @Schema(description = "페이지 번호 (0부터 시작, 기본값: 0)", example = "0")
    private Integer page = 0;

    @Schema(description = "한 페이지당 결과 개수 (기본값: 10, 최대: 50)", example = "10")
    private Integer size = 10;
}
