package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.project.enums.AgeLimit;
import org.muses.backendbulidtest251228.domain.project.enums.Region;

import java.util.List;

@Schema(description = "1단계: 개요 저장 요청 DTO - 프로젝트 기본 정보 설정")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutlineRequestDT {

    @Schema(description = "프로젝트 제목 (필수)", example = "인디밴드 단독 콘서트")
    private String title;

    @Schema(description = "프로젝트 간략 소개 (한 줄 설명)", example = "인디밴드 단독 콘서트입니다")
    private String description;

    @Schema(description = "대표 이미지 URL (썸네일)")
    private String thumbnailUrl;

    @Schema(
            description = "프로젝트 태그 목록 (검색 및 분류용)",
            example = "[\"음악\", \"콘서트\", \"인디밴드\"]"
    )
    private List<String> tags;

    @Schema(
            description = "연령 제한 설정 (ALL: 전체이용가, ADULT: 19세 이상)",
            example = "ALL",
            allowableValues = {"ALL", "ADULT"}
    )
    private AgeLimit ageLimit;

    @Schema(
            description = "공연/행사 지역",
            example = "SEOUL",
            allowableValues = {"SEOUL", "GYEONGGI", "BUSAN", "DAEGU", "INCHEON", "GWANGJU", "DAEJEON", "ULSAN", "SEJONG", "GANGWON", "CHUNGBUK", "CHUNGNAM", "JEONBUK", "JEONNAM", "GYEONGBUK", "GYEONGNAM", "JEJU"}
    )
    private Region region;
}
