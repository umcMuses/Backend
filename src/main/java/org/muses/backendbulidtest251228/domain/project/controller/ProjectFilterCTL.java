package org.muses.backendbulidtest251228.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.muses.backendbulidtest251228.domain.project.enums.AgeLimit;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.enums.Region;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NOTE: ERD에 없는 기능 추가 - 클라이언트에서 필터 표시에 필요한 정보 제공
 * 프로젝트 관련 Enum 목록 제공 (지역, 펀딩상태, 연령제한)
 */
@Tag(name = "ProjectFilters", description = "프로젝트 필터 옵션 API")
@RestController
@RequestMapping("/api/project-filters")
public class ProjectFilterCTL {

    @Operation(summary = "지역 목록 조회", description = "프로젝트 지역 필터 옵션")
    @GetMapping("/regions")
    public ApiResponse<List<Map<String, String>>> getRegions() {
        return ApiResponse.success(getRegionList());
    }

    @Operation(summary = "펀딩 상태 목록 조회", description = "프로젝트 펀딩 상태 필터 옵션")
    @GetMapping("/funding-statuses")
    public ApiResponse<List<Map<String, String>>> getFundingStatuses() {
        return ApiResponse.success(getFundingStatusList());
    }

    @Operation(summary = "연령 제한 목록 조회", description = "프로젝트 개설 시 연령 제한 옵션")
    @GetMapping("/age-limits")
    public ApiResponse<List<Map<String, String>>> getAgeLimits() {
        return ApiResponse.success(getAgeLimitList());
    }

    private List<Map<String, String>> getRegionList() {
        return Arrays.stream(Region.values())
                .map(e -> Map.of("code", e.name(), "name", e.getDisplayName()))
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> getFundingStatusList() {
        return Arrays.stream(FundingStatus.values())
                .map(e -> Map.of("code", e.name(), "name", e.getDescription()))
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> getAgeLimitList() {
        return Arrays.stream(AgeLimit.values())
                .map(e -> Map.of("code", e.name(), "name", e.getDescription()))
                .collect(Collectors.toList());
    }
}
