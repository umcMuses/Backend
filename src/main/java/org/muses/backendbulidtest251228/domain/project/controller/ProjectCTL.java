package org.muses.backendbulidtest251228.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.domain.project.dto.request.FundingRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.InfoRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.OutlineRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.ProjectCreateRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.ProjectSearchRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.RewardsRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.request.StoryRequestDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectCardResponseDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectDetailResponseDTO;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectListResponseDTO;
import org.muses.backendbulidtest251228.domain.project.service.ProjectSRV;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Project", description = "프로젝트 개설 API")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectCTL {

    private final ProjectSRV projectSRV;

    // ==================== 프로젝트 초기 생성 ====================

    @Operation(summary = "프로젝트 초기 생성 (최초 진입)", description = "빈 프로젝트를 먼저 생성하여 projectId를 발급, status는 DRAFT")
    @PostMapping("/draft")
    public ApiResponse<Map<String, Object>> createDraft(
            @RequestBody ProjectCreateRequestDTO request
    ) {
        Long projectId = projectSRV.createProject(request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "status", "DRAFT",
                "message", "프로젝트 생성 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 프로젝트 상세 조회 (임시저장 데이터 통합 조회) ====================

    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 전체 정보 조회 (임시저장 데이터 포함)")
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectDetailResponseDTO> getProjectDetail(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId
    ) {
        ProjectDetailResponseDTO response = projectSRV.getProjectDetail(projectId);
        return ApiResponse.success(response);
    }

    // ==================== 프로젝트 목록 조회 (카드용) ====================

    @Operation(summary = "프로젝트 목록 조회", description = "공개된 프로젝트 카드 목록 조회 (포스터, 제목, 달성률, D-day)")
    @GetMapping
    public ApiResponse<List<ProjectCardResponseDTO>> getProjectList() {
        List<ProjectCardResponseDTO> response = projectSRV.getProjectList();
        return ApiResponse.success(response);
    }

    // ==================== 프로젝트 검색 (필터링 + 페이징) ====================

    @Operation(summary = "프로젝트 검색", description = "필터링 + 페이징 지원 (지역, 상태, 태그, 키워드)")
    @GetMapping("/search")
    public ApiResponse<ProjectListResponseDTO> searchProjects(
            @Parameter(description = "지역 필터 (SEOUL, GYEONGGI, BUSAN, DAEGU, INCHEON, GWANGJU, DAEJEON, ULSAN, SEJONG, GANGWON, CHUNGBUK, CHUNGNAM, JEONBUK, JEONNAM, GYEONGBUK, GYEONGNAM, JEJU...)")
            @RequestParam(required = false) String region,

            @Parameter(description = "펀딩 상태 필터 (PREPARING: 준비중, SCHEDULED: 오픈 예정, FUNDING: 펀딩중, SUCCESS: 성공, FAIL: 실패, CANCELLED: 취소)")
            @RequestParam(required = false) String fundingStatus,

            @Parameter(description = "태그 검색 - project_tags 테이블에서 부분 일치 검색 (예: '음악' 입력 시 '음악', '음악회' 등 매칭)")
            @RequestParam(required = false) String tag,

            @Parameter(description = "키워드 검색 - 제목(title)과 설명(description)에서 부분 일치 검색 (예: '콘서트' 입력 시 제목 또는 설명에 '콘서트' 포함된 프로젝트 매칭)")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "페이지 크기 (기본값: 10)")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        ProjectSearchRequestDTO request = new ProjectSearchRequestDTO(region, fundingStatus, tag, keyword, page, size);
        ProjectListResponseDTO response = projectSRV.searchProjects(request);
        return ApiResponse.success(response);
    }

    // ==================== 1단계: 개요 ====================

    @Operation(summary = "1단계: 개요 저장", description = "프로젝트 개요 정보 저장 (제목, 카테고리, 태그, 대표이미지 등)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content)
    })
    @PutMapping("/{projectId}/outline")
    public ApiResponse<Map<String, Object>> saveOutline(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody OutlineRequestDTO request
    ) {
        projectSRV.saveOutline(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "개요 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 2단계: 펀딩 ====================

    @Operation(summary = "2단계: 펀딩 저장", description = "펀딩 설정 저장 (목표금액, 마감일, 펀딩방식 등)")
    @PutMapping("/{projectId}/funding")
    public ApiResponse<Map<String, Object>> saveFunding(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody FundingRequestDTO request
    ) {
        projectSRV.saveFunding(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "펀딩 설정 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 3단계: 리워드 ====================

    @Operation(summary = "3단계: 리워드 저장", description = "리워드 목록 저장 (기존 리워드 삭제 후 새로 저장)")
    @PutMapping("/{projectId}/rewards")
    public ApiResponse<Map<String, Object>> saveRewards(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody RewardsRequestDTO request
    ) {
        projectSRV.saveRewards(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "리워드 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 4단계: 스토리 ====================

    @Operation(summary = "4단계: 스토리 본문 저장", description = "완성된 HTML/Markdown 텍스트 저장")
    @PutMapping("/{projectId}/story")
    public ApiResponse<Map<String, Object>> saveStory(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody StoryRequestDTO request
    ) {
        projectSRV.saveStory(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "스토리 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // TODO 이미지나 파일 업로드는 따로 분류해놓음
    // 파일 저장 로직 확정되면 본문 저장과 합칠 예정
    @Operation(summary = "4단계: 이미지 업로드", description = "스토리 본문용 이미지 업로드")
    @PostMapping(value = "/{projectId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> uploadImage(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestPart("image") MultipartFile image
    ) {
        String imageUrl = projectSRV.uploadImage(projectId, image);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "imageUrl", imageUrl,
                "message", "이미지 업로드 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 5단계: 정보 ====================

    @Operation(summary = "5단계: 정보 저장", description = "진행자/담당자 정보 저장")
    @PutMapping("/{projectId}/info")
    public ApiResponse<Map<String, Object>> saveInfo(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestBody InfoRequestDTO request
    ) {
        projectSRV.saveInfo(projectId, request);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "message", "정보 저장 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @Operation(summary = "5단계: 정산 서류 이미지 업로드", description = "신분증/사업자등록증 사본, 통장 사본 업로드")
    @PostMapping(value = "/{projectId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> uploadDocuments(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId,
            @RequestPart("idCard") MultipartFile idCard,
            @RequestPart("bankbook") MultipartFile bankbook
    ) {
        List<String> documentUrls = projectSRV.uploadDocuments(projectId, idCard, bankbook);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "documentUrls", documentUrls,
                "message", "정산 서류 업로드 완료",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ==================== 프로젝트 제출 ====================

    @Operation(summary = "프로젝트 제출하기", description = "전체 유효성 검사 후 심사 요청 (status: DRAFT => PENDING)")
    @PostMapping("/{projectId}/submit")
    public ApiResponse<Map<String, Object>> submitProject(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable Long projectId
    ) {
        projectSRV.submitProject(projectId);
        return ApiResponse.success(Map.of(
                "projectId", projectId,
                "status", "PENDING",
                "message", "프로젝트가 심사 요청되었습니다",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
